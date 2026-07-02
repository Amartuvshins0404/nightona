import asyncio

from nightona import AsyncNightona, ListSandboxesQuery, SandboxListSortDirection, SandboxListSortField, SandboxState


async def main():
    async with AsyncNightona() as nightona:
        print("Creating sandbox")
        sandbox = await nightona.create()
        print("Sandbox created")

        _ = await sandbox.set_labels(
            {
                "public": "true",
            }
        )

        print("Stopping sandbox")
        await nightona.stop(sandbox)
        print("Sandbox stopped")

        print("Starting sandbox")
        await nightona.start(sandbox)
        print("Sandbox started")

        print("Getting existing sandbox")
        existing_sandbox = await nightona.get(sandbox.id)
        print("Get existing sandbox")

        response = await existing_sandbox.process.exec('echo "Hello World from exec!"', cwd="/home/nightona", timeout=10)
        if response.exit_code != 0:
            print(f"Error: {response.exit_code} {response.result}")
        else:
            print(response.result)

        async for sb in nightona.list(
            ListSandboxesQuery(
                limit=10,
                labels={"env": "dev"},
                states=[SandboxState.STARTED],
                sort=SandboxListSortField.CREATEDAT,
                order=SandboxListSortDirection.DESC,
            )
        ):
            print(sb.id)

        print("Removing sandbox")
        await nightona.delete(sandbox)
        print("Sandbox removed")


if __name__ == "__main__":
    asyncio.run(main())
