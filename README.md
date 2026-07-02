# Nightona — the open-source continuation of Daytona

**Nightona is a community-maintained, open-source fork of [Daytona](https://github.com/daytonaio/daytona): secure, elastic sandbox infrastructure for running AI-generated code and AI agent workflows.** When Daytona moved its core development to a private codebase in June 2026, this project picked up the last open release — **v0.190.0, licensed under AGPL-3.0** — to keep a fully open-source AI code execution sandbox alive, self-hostable, and community-driven.

If you are looking for an **open-source Daytona alternative**, a **self-hosted sandbox for AI agents**, or a way to keep running the Daytona open-source stack you already depend on, you are in the right place.

## What happened to Daytona's open-source repository?

In June 2026, the Daytona team archived active development of [`daytonaio/daytona`](https://github.com/daytonaio/daytona) and moved core development to a private codebase. The public repository was reduced to a maintenance notice; it receives no further updates, fixes, or releases.

The last complete open-source release was **v0.190.0**, published under the **GNU Affero General Public License v3.0 (AGPL-3.0)** — a license that guarantees the code remains free to use, study, modify, and redistribute.

## What is Nightona?

Nightona continues that codebase as an independent open-source project:

- **Full continuation, not a snapshot.** This repository preserves the complete Daytona git history (2,700+ commits) and continues development from the v0.190.0 tag.
- **Open source, permanently.** Nightona is licensed under [AGPL-3.0](LICENSE), the same license as the code it inherits. The copyleft terms ensure it can never be taken closed-source.
- **Self-hosted first.** The priority is the open-source deployment path: running the full sandbox stack on your own infrastructure with Docker Compose or Kubernetes, with no managed-service dependency.
- **Community-maintained.** Bug fixes, security patches, and new features are developed in the open. Contributions are welcome — see [Contributing](#contributing).

> **Status:** Nightona is in its bootstrap phase. The codebase is functionally identical to Daytona v0.190.0, and the rebrand (package names, module paths, binaries, UI) is in progress. Until that lands, code, imports, and docs still reference the Daytona name. Daytona is a trademark of its respective owners; Nightona is not affiliated with or endorsed by Daytona.

## What does it do?

Nightona is a secure and elastic infrastructure runtime for AI-generated code execution and agent workflows. It provides **sandboxes** — full composable computers with complete isolation, a dedicated kernel, filesystem, network stack, and allocated vCPU, RAM, and disk.

Sandboxes spin up in under 90ms from code to execution and run code in Python, TypeScript, and JavaScript. Built on OCI/Docker compatibility, massive parallelization, and unlimited persistence, they deliver consistent, predictable environments for AI agent workflows.

Agents and developers interact with sandboxes programmatically through SDKs (Python, TypeScript, Ruby, Go, Java), a REST API, and a CLI. Operations span sandbox lifecycle management, filesystem operations, process and code execution, and runtime configuration through base images, packages, and tooling. Stateful environment snapshots enable persistent agent operations across sessions.

### Features

- **Platform**: organizations, API keys, limits, audit logs, OpenTelemetry
- **Sandboxes**: isolated environments, snapshots, declarative builder, volumes, regions
- **Agent tools**: process & code execution, filesystem operations, git operations, LSP, computer use, MCP server, PTY, log streaming
- **Human tools**: dashboard, web terminal, SSH access, VNC access, preview URLs
- **System tools**: webhooks, network limits

## Architecture

The platform is organized into three planes:

- **Interface plane**: client interfaces (SDKs, API, CLI, dashboard)
- **Control plane**: orchestrates all sandbox operations
- **Compute plane**: runs and manages sandbox instances

### Applications

Runnable applications and services, each a deployable or buildable component in the [apps](apps) directory:

- [`api`](apps/api): NestJS-based RESTful service; primary entry point for all platform operations
- [`cli`](apps/cli): Go command-line interface for interacting with sandboxes
- [`daemon`](apps/daemon): code execution agent that runs inside each sandbox
- [`dashboard`](apps/dashboard): web user interface for visual sandbox management
- [`docs`](apps/docs): documentation content
- [`otel-collector`](apps/otel-collector): trace and metric collection for SDK operations
- [`proxy`](apps/proxy): reverse proxy for custom routing and preview URLs
- [`runner`](apps/runner): compute nodes that power the compute plane and run sandboxes
- [`snapshot-manager`](apps/snapshot-manager): orchestrates the creation of sandbox snapshots
- [`ssh-gateway`](apps/ssh-gateway): standalone SSH gateway for authenticated `ssh` connections

### Client libraries

Developer-facing SDKs backed by OpenAPI-generated REST clients, in the [libs](libs) directory:

- **Python**: [`sdk-python`](libs/sdk-python) and API clients
- **TypeScript**: [`sdk-typescript`](libs/sdk-typescript) and API clients
- **Ruby**: [`sdk-ruby`](libs/sdk-ruby) and API clients
- **Go**: [`sdk-go`](libs/sdk-go) and API clients
- **Java**: [`sdk-java`](libs/sdk-java) and API clients

> Published package names are inherited from Daytona v0.190.0 and will change as part of the rebrand. Until Nightona publishes its own packages, build the SDKs from source.

## Self-hosted deployment

The full local stack can be run from the [`docker`](docker) directory using Docker Compose, and Helm charts are available in [`charts`](charts). This — the open-source, self-hosted deployment — is the core focus of Nightona.

## Development

### Devcontainer (full environment)

Open this repository in a [devcontainer](https://containers.dev/)-compatible editor (VS Code, GitHub Codespaces) for a batteries-included setup with all languages, tools, and supporting services.

### Nix (lightweight, agent-friendly)

```bash
# Enter the full dev shell (Go + Node + Python + Ruby + JDK)
nix develop

# Or pick a language-specific shell
nix develop .#go       # Go services & libs
nix develop .#node     # TypeScript / Node.js apps & libs
nix develop .#python   # Python SDKs & libs
nix develop .#ruby     # Ruby SDKs & libs
nix develop .#java     # Java SDKs & libs
```

**Prerequisites:** [Nix](https://nixos.org/download/) with flakes enabled. See [`AGENTS.md`](AGENTS.md) for the full shell reference and common commands.

> **Note:** Supporting services (PostgreSQL, Redis, etc.) are managed via `docker compose -f .devcontainer/docker-compose.yaml up`.

## Roadmap

1. **Rebrand**: rename packages, module paths, binaries, environment variables, and UI from Daytona to Nightona
2. **Standalone builds**: publish Nightona-named packages, container images, and Helm charts
3. **Maintenance**: dependency updates, security patches, and bug fixes on the v0.190.0 base
4. **Community**: issue triage, contribution workflow, and open governance

## FAQ

**Is Nightona affiliated with Daytona?**
No. Nightona is an independent community fork. "Daytona" is a trademark of its respective owners; Nightona is not affiliated with, sponsored by, or endorsed by Daytona.

**Is this legal?**
Yes. Daytona v0.190.0 was released under AGPL-3.0, which explicitly grants the right to fork, modify, and redistribute the software. Nightona preserves the original [LICENSE](LICENSE), [COPYRIGHT](COPYRIGHT), and [NOTICE](NOTICE) attributions as the license requires.

**Can I migrate from Daytona open source to Nightona?**
Yes. Nightona starts from the exact v0.190.0 codebase, so existing open-source deployments are directly compatible. Migration guides will accompany the rebrand.

**Why "Nightona"?**
Because the sun set on the open-source Daytona — and this project keeps the lights on.

## Contributing

Nightona is open source under the [GNU Affero General Public License v3.0](LICENSE) and is the copyright of its [contributors](NOTICE), including the original Daytona authors. To contribute, read the [Developer Certificate of Origin Version 1.1](https://developercertificate.org/) and the [contributing guide](CONTRIBUTING.md).

## License & attribution

Nightona is a derivative work of Daytona v0.190.0 by Daytona Platforms Inc. and its contributors, used under the terms of the [AGPL-3.0](LICENSE). Original copyright notices are preserved in [COPYRIGHT](COPYRIGHT) and [NOTICE](NOTICE). All modifications by the Nightona project are likewise licensed under AGPL-3.0.
