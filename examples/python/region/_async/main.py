import asyncio
import time

from nightona import AsyncNightona, CreateSandboxFromSnapshotParams, CreateSnapshotParams, NightonaConfig, Image


async def main():
    async with AsyncNightona(NightonaConfig(target="us")) as nightona:
        snapshot1 = f"us-{int(time.time() * 1000)}"
        print(f"Creating snapshot {snapshot1}")
        try:
            _ = await nightona.snapshot.create(
                CreateSnapshotParams(
                    name=snapshot1,
                    image=Image.debian_slim("3.12"),
                    region_id="us",
                )
            )
        except Exception as e:
            print(e)
        print("--------------------------------")

        snapshot2 = f"eu-{int(time.time() * 1000)}"
        print(f"Creating snapshot {snapshot2}")
        try:
            _ = await nightona.snapshot.create(
                CreateSnapshotParams(
                    name=snapshot2,
                    image=Image.debian_slim("3.13"),
                    region_id="eu",
                )
            )
        except Exception as e:
            print("error", e)
        print("--------------------------------")

        print(f"Creating sandbox from snapshot {snapshot1}")
        try:
            sandbox = await nightona.create(CreateSandboxFromSnapshotParams(snapshot=snapshot1))
            await nightona.delete(sandbox)
        except Exception as e:
            print(e)
        print("--------------------------------")

        print(f"Creating sandbox from snapshot {snapshot2}")
        try:
            sandbox = await nightona.create(CreateSandboxFromSnapshotParams(snapshot=snapshot2))
            await nightona.delete(sandbox)
        except Exception as e:
            print("error", e)


if __name__ == "__main__":
    asyncio.run(main())
