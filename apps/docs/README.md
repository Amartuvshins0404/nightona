<div align="center">

[![Documentation](https://img.shields.io/github/v/release/Amartuvshins0404/nightona?label=Docs&color=23cc71)](https://www.daytona.io/docs)
![License](https://img.shields.io/badge/License-AGPL--3-blue)
[![Go Report Card](https://goreportcard.com/badge/github.com/Amartuvshins0404/nightona)](https://goreportcard.com/report/github.com/Amartuvshins0404/nightona)
[![Issues - nightona](https://img.shields.io/github/issues/Amartuvshins0404/nightona)](https://github.com/Amartuvshins0404/nightona/issues)
![GitHub Release](https://img.shields.io/github/v/release/Amartuvshins0404/nightona)

</div>

&nbsp;

<div align="center">
  <picture>
    <source media="(prefers-color-scheme: dark)" srcset="https://github.com/Amartuvshins0404/nightona/raw/main/assets/images/Nightona-logotype-white.png">
    <source media="(prefers-color-scheme: light)" srcset="https://github.com/Amartuvshins0404/nightona/raw/main/assets/images/Nightona-logotype-black.png">
    <img alt="Nightona logo" src="https://github.com/Amartuvshins0404/nightona/raw/main/assets/images/Nightona-logotype-black.png" width="50%">
  </picture>
</div>

<h3 align="center">
  Run AI Code.
  <br/>
  Secure and Elastic Infrastructure for
  Running Your AI-Generated Code.
</h3>

<p align="center">
    <a href="https://www.daytona.io/docs"> Documentation </a>·
    <a href="https://github.com/Amartuvshins0404/nightona/issues/new?assignees=&labels=bug&projects=&template=bug_report.md&title=%F0%9F%90%9B+Bug+Report%3A+"> Report Bug </a>·
    <a href="https://github.com/Amartuvshins0404/nightona/issues/new?assignees=&labels=enhancement&projects=&template=feature_request.md&title=%F0%9F%9A%80+Feature%3A+"> Request Feature </a>·
    <a href="https://go.daytona.io/slack"> Join our Slack </a>·
    <a href="https://github.com/Amartuvshins0404/nightona/discussions"> GitHub Discussions </a>
</p>

<p align="center">
    <a href="https://www.producthunt.com/posts/nightona-2?embed=true&utm_source=badge-top-post-badge&utm_medium=badge&utm_souce=badge-nightona&#0045;2" target="_blank"><img src="https://api.producthunt.com/widgets/embed-image/v1/top-post-badge.svg?post_id=957617&theme=neutral&period=daily&t=1746176740150" alt="Nightona&#0032; - Secure&#0032;and&#0032;elastic&#0032;infra&#0032;for&#0032;running&#0032;your&#0032;AI&#0045;generated&#0032;code&#0046; | Product Hunt" style="width: 250px; height: 54px;" width="250" height="54" /></a>
    <a href="https://www.producthunt.com/posts/nightona-2?embed=true&utm_source=badge-top-post-topic-badge&utm_medium=badge&utm_souce=badge-nightona&#0045;2" target="_blank"><img src="https://api.producthunt.com/widgets/embed-image/v1/top-post-topic-badge.svg?post_id=957617&theme=neutral&period=monthly&topic_id=237&t=1746176740150" alt="Nightona&#0032; - Secure&#0032;and&#0032;elastic&#0032;infra&#0032;for&#0032;running&#0032;your&#0032;AI&#0045;generated&#0032;code&#0046; | Product Hunt" style="width: 250px; height: 54px;" width="250" height="54" /></a>
</p>

---

## Installation

### Python SDK

```bash
pip install nightona
```

### TypeScript SDK

```bash
npm install @nightona/sdk
```

---

## Features

- **Lightning-Fast Infrastructure**: Sub-90ms Sandbox creation from code to execution.
- **Separated & Isolated Runtime**: Execute AI-generated code with zero risk to your infrastructure.
- **Massive Parallelization for Concurrent AI Workflows**: Fork Sandbox filesystem and memory state (Coming soon!)
- **Programmatic Control**: File, Git, LSP, and Execute API
- **Unlimited Persistence**: Your Sandboxes can live forever
- **OCI/Docker Compatibility**: Use any OCI/Docker image to create a Sandbox

---

## Quick Start

1. Create an account at https://app.daytona.io
1. Generate a [new API key](https://app.daytona.io/dashboard/keys)
1. Follow the [Getting Started docs](https://www.daytona.io/docs/getting-started/) to start using the Nightona SDK

## Creating your first Sandbox

### Python SDK

```py
from nightona import Nightona, NightonaConfig, CreateSandboxBaseParams

# Initialize the Nightona client
nightona = Nightona(NightonaConfig(api_key="YOUR_API_KEY"))

# Create the Sandbox instance
sandbox = nightona.create(CreateSandboxBaseParams(language="python"))

# Run code securely inside the Sandbox
response = sandbox.process.code_run('print("Sum of 3 and 4 is " + str(3 + 4))')
if response.exit_code != 0:
    print(f"Error running code: {response.exit_code} {response.result}")
else:
    print(response.result)

# Clean up the Sandbox
nightona.delete(sandbox)
```

### Typescript SDK

```jsx
import { Nightona } from '@nightona/sdk'

async function main() {
  // Initialize the Nightona client
  const nightona = new Nightona({
    apiKey: 'YOUR_API_KEY',
  })

  let sandbox
  try {
    // Create the Sandbox instance
    sandbox = await nightona.create({
      language: 'typescript',
    })
    // Run code securely inside the Sandbox
    const response = await sandbox.process.codeRun('console.log("Sum of 3 and 4 is " + (3 + 4))')
    if (response.exitCode !== 0) {
      console.error('Error running code:', response.exitCode, response.result)
    } else {
      console.log(response.result)
    }
  } catch (error) {
    console.error('Sandbox flow error:', error)
  } finally {
    if (sandbox) await nightona.delete(sandbox)
  }
}

main().catch(console.error)
```
