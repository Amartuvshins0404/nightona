import { Nightona, Image } from '@nightona-co/sdk'
import fs from 'fs'

async function main() {
  const nightona = new Nightona()

  // Generate unique name for the snapshot to avoid conflicts
  const snapshotName = `node-example:${Date.now()}`
  console.log(`Creating snapshot with name: ${snapshotName}`)

  // Create a local file with some data
  const localFilePath = 'file_example.txt'
  const localFileContent = 'Hello, World!'
  fs.writeFileSync(localFilePath, localFileContent)

  // Create a Python image with common data science packages
  const image = Image.debianSlim('3.12')
    .pipInstall(['numpy', 'pandas', 'matplotlib', 'scipy', 'scikit-learn'])
    .runCommands('apt-get update && apt-get install -y git', 'mkdir -p /home/nightona/workspace')
    .workdir('/home/nightona/workspace')
    .env({
      MY_ENV_VAR: 'My Environment Variable',
    })
    .addLocalFile(localFilePath, '/home/nightona/workspace/file_example.txt')

  // Create the snapshot
  console.log(`=== Creating Snapshot: ${snapshotName} ===`)
  await nightona.snapshot.create(
    {
      name: snapshotName,
      image,
      resources: {
        cpu: 1,
        memory: 1,
        disk: 3,
      },
    },
    {
      onLogs: console.log,
    },
  )

  // Create first sandbox using the pre-built image
  console.log('\n=== Creating Sandbox from Pre-built Snapshot ===')
  const sandbox1 = await nightona.create({
    snapshot: snapshotName,
  })

  try {
    // Verify the first sandbox environment
    console.log('Verifying sandbox from pre-built image:')
    const nodeResponse = await sandbox1.process.executeCommand('python --version && pip list')
    console.log('Python environment:')
    console.log(nodeResponse.result)

    // Verify the file was added to the image
    const fileContent = await sandbox1.process.executeCommand('cat file_example.txt')
    console.log('File content:')
    console.log(fileContent.result)
  } finally {
    // Clean up first sandbox
    await nightona.delete(sandbox1)
  }

  // Create second sandbox with a new dynamic image
  console.log('\n=== Creating Sandbox with Dynamic Image ===')

  // Define a new dynamic image for the second sandbox
  const dynamicImage = Image.debianSlim('3.13')
    .pipInstall(['pytest', 'pytest-cov', 'black', 'isort', 'mypy', 'ruff'])
    .runCommands('apt-get update && apt-get install -y git', 'mkdir -p /home/nightona/project')
    .workdir('/home/nightona/project')
    .env({
      NODE_ENV: 'development',
    })

  // Create sandbox with the dynamic image
  const sandbox2 = await nightona.create(
    {
      image: dynamicImage,
    },
    {
      timeout: 0,
      onSnapshotCreateLogs: console.log,
    },
  )

  try {
    // Verify the second sandbox environment
    console.log('Verifying sandbox with dynamic image:')
    const toolsResponse = await sandbox2.process.executeCommand('pip list | grep -E "pytest|black|isort|mypy|ruff"')
    console.log('Development tools:')
    console.log(toolsResponse.result)
  } finally {
    // Clean up second sandbox
    await nightona.delete(sandbox2)
  }
}

main().catch((error) => {
  console.error('Error:', error)
  process.exit(1)
})
