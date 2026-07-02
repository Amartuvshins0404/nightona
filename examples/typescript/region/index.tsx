import { Nightona, Image } from '@nightona/sdk'

async function main() {
  const nightona = new Nightona({
    target: 'us',
  })

  const snapshot1 = `us-${Date.now()}`
  console.log(`Creating snapshot ${snapshot1}`)
  try {
    await nightona.snapshot.create({
      name: snapshot1,
      image: Image.debianSlim('3.12'),
      regionId: 'us',
    })
  } catch (error: any) {
    console.error(error?.message)
  }
  console.log('--------------------------------')

  const snapshot2 = `eu-${Date.now()}`
  console.log(`Creating snapshot ${snapshot2}`)
  try {
    await nightona.snapshot.create({
      name: snapshot2,
      image: Image.debianSlim('3.13'),
      regionId: 'eu',
    })
  } catch (error: any) {
    console.error('error', error?.message)
  }
  console.log('--------------------------------')

  console.log(`Creating sandbox from snapshot ${snapshot1}`)
  try {
    const sandbox = await nightona.create({
      snapshot: snapshot1,
    })
    await nightona.delete(sandbox)
  } catch (error: any) {
    console.error(error?.message)
  }
  console.log('--------------------------------')

  console.log(`Creating sandbox from snapshot ${snapshot2}`)
  try {
    const sandbox = await nightona.create({
      snapshot: snapshot2,
    })
    await nightona.delete(sandbox)
  } catch (error: any) {
    console.error('error', error?.message)
  }
}

main().catch(console.error)
