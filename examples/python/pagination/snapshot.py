from nightona import Nightona


def main():
    nightona = Nightona()

    result = nightona.snapshot.list(page=2, limit=10)
    for snapshot in result.items:
        print(f"{snapshot.name} ({snapshot.image_name})")


if __name__ == "__main__":
    main()
