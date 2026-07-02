import asyncio

from nightona import AsyncNightona, CreateSandboxFromSnapshotParams


async def main():
    async with AsyncNightona() as nightona:
        # Default interval
        sandbox1 = await nightona.create()
        print(sandbox1.auto_archive_interval)

        # Set interval to 1 hour
        await sandbox1.set_auto_archive_interval(60)
        print(sandbox1.auto_archive_interval)

        # Max interval
        sandbox2 = await nightona.create(params=CreateSandboxFromSnapshotParams(auto_archive_interval=0))
        print(sandbox2.auto_archive_interval)

        # 1 day interval
        sandbox3 = await nightona.create(params=CreateSandboxFromSnapshotParams(auto_archive_interval=1440))
        print(sandbox3.auto_archive_interval)

        await sandbox1.delete()
        await sandbox2.delete()
        await sandbox3.delete()


if __name__ == "__main__":
    asyncio.run(main())
