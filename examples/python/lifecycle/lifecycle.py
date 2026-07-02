from nightona import ListSandboxesQuery, Nightona, SandboxListSortDirection, SandboxListSortField, SandboxState


def main():
    nightona = Nightona()

    print("Creating sandbox")
    sandbox = nightona.create()
    print("Sandbox created")

    _ = sandbox.set_labels(
        {
            "public": "true",
        }
    )

    print("Stopping sandbox")
    nightona.stop(sandbox)
    print("Sandbox stopped")

    print("Starting sandbox")
    nightona.start(sandbox)
    print("Sandbox started")

    print("Getting existing sandbox")
    existing_sandbox = nightona.get(sandbox.id)
    print("Get existing sandbox")

    response = existing_sandbox.process.exec('echo "Hello World from exec!"', cwd="/home/nightona", timeout=10)
    if response.exit_code != 0:
        print(f"Error: {response.exit_code} {response.result}")
    else:
        print(response.result)

    for sb in nightona.list(
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
    nightona.delete(sandbox)
    print("Sandbox removed")


if __name__ == "__main__":
    main()
