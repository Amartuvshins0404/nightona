from nightona import ListSandboxesQuery, Nightona, SandboxListSortDirection, SandboxListSortField, SandboxState


def main():
    nightona = Nightona()

    for sandbox in nightona.list(
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
    main()
