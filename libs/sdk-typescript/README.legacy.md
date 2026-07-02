# @daytonaio/sdk is now @nightona/sdk

> **This package has been renamed.** Please use [`@nightona/sdk`](https://www.npmjs.com/package/@nightona/sdk) instead.

## Migration

Update your dependency:

```bash
npm uninstall @daytonaio/sdk
npm install @nightona/sdk
```

or with yarn:

```bash
yarn remove @daytonaio/sdk
yarn add @nightona/sdk
```

Then update your imports:

```diff
- import { Nightona } from '@daytonaio/sdk'
+ import { Nightona } from '@nightona/sdk'
```

The API is identical — only the package name has changed.

## About @nightona/sdk

The official TypeScript SDK for [Nightona](https://daytona.io), secure and elastic infrastructure for running AI-generated code.

For documentation, examples, and guides, visit [daytona.io/docs](https://www.daytona.io/docs/en/typescript-sdk/).
