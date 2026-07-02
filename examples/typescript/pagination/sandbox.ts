import { Nightona, SandboxListSortDirection, SandboxListSortField, SandboxState } from '@nightona-co/sdk'

async function main() {
  const nightona = new Nightona()

  for await (const sandbox of nightona.list({
    limit: 10,
    labels: { env: 'dev' },
    states: [SandboxState.STARTED],
    sort: SandboxListSortField.CREATED_AT,
    order: SandboxListSortDirection.DESC,
  })) {
    console.log(sandbox.id)
  }
}

main().catch(console.error)
