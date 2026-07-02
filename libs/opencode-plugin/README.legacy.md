# @daytonaio/opencode is now @nightona/opencode

> **This package has been renamed.** Please use [`@nightona/opencode`](https://www.npmjs.com/package/@nightona/opencode) instead.

## Migration

Update your OpenCode configuration:

```diff
{
  "$schema": "https://opencode.ai/config.json",
- "plugin": ["@daytonaio/opencode"]
+ "plugin": ["@nightona/opencode"]
}
```

The plugin is identical — only the package name has changed.

## About @nightona/opencode

An OpenCode plugin that automatically runs all sessions in Nightona sandboxes for isolated, reproducible development environments.

For documentation and setup instructions, see the [@nightona/opencode README](https://www.npmjs.com/package/@nightona/opencode).
