import asyncio

from nightona import AsyncNightona, ListSandboxesQuery, SandboxListSortDirection, SandboxListSortField, SandboxState


async def main():
    async with AsyncNightona() as nightona:
        async for sandbox in nightona.list(
            ListSandboxesQuery(
                limit=10,
                labels={"env": "dev"},
                states=[SandboxState.STARTED],
                sort=SandboxListSortField.CREATEDAT,
                order=SandboxListSortDirection.DESC,
            )
        ):
            print(sandbox.id)


if __name__ == "__main__":
    asyncio.run(main())
