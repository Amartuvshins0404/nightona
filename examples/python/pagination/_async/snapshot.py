import asyncio

from nightona import AsyncNightona


async def main():
    async with AsyncNightona() as nightona:
        result = await nightona.snapshot.list(page=2, limit=10)
        for snapshot in result.items:
            print(f"{snapshot.name} ({snapshot.image_name})")


if __name__ == "__main__":
    asyncio.run(main())
