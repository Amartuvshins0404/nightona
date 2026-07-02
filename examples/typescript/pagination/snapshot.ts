import { Nightona } from '@nightona/sdk'

async function main() {
  const nightona = new Nightona()

  const result = await nightona.snapshot.list(2, 10)
  console.log(`Found ${result.total} snapshots`)
  result.items.forEach((snapshot) => console.log(`${snapshot.name} (${snapshot.imageName})`))
}

main().catch(console.error)
