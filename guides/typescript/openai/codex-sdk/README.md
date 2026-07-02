# Codex Agent

## Overview

This example runs an [OpenAI Codex](https://chatgpt.com/features/codex) agent inside a Nightona sandbox. You can interact with the agent via the CLI to run automations, build apps, and launch web apps or services using [Nightona preview links](https://www.daytona.io/docs/en/preview-and-authentication/#fetching-a-preview-link).

> Note: In this example, your OpenAI API key is passed into the sandbox environment and may be accessible to any code executed within it.

## Features

- **Secure sandbox execution:** The agent operates within a controlled environment, along with code or commands run by the agent.
- **Codex integration:** Includes the full abilities of the Codex SDK, including reading and editing code files, and running shell commands.
- **Preview deployed apps:** Use Nightona preview links to view and interact with your deployed applications.

## Prerequisites

- **Node.js:** Version 18 or higher is required

## Environment Variables

To run this example, you need to set the following environment variables:

- `NIGHTONA_API_KEY`: Required for access to Nightona sandboxes. Get it from [Nightona Dashboard](https://app.daytona.io/dashboard/keys)
- `SANDBOX_OPENAI_API_KEY`: Required to run Codex. Get it from [OpenAI Developer Platform](https://platform.openai.com/api-keys)

Create a `.env` file in the project directory with these variables.

## Getting Started

### Setup and Run

1. Install dependencies:

   ```bash
   npm install
   ```

2. Run the example:

   ```bash
   npm run start
   ```

## How It Works

When this example is run, the agent follows the following workflow:

1. A new Nightona sandbox is created.
2. The coding agent is installed and launched inside the sandbox.
3. User queries are passed to the agent, and the result is displayed to the user.
4. When the script is terminated, the sandbox is deleted.

## Example Output

```
Creating sandbox...
Installing Codex agent in sandbox...
Press Ctrl+C at any time to exit.
User: make a lunar lander web app
Thinking...
🔨 ✓ Run: /bin/sh -lc ls
🔨 ✓ Run: /bin/sh -lc pwd
🔨 ✓ Run: /bin/sh -lc '/bin/ls -a'
🔨 ✓ Run: /bin/sh -lc '/bin/ls -a .nightona'
📝 Add /home/daytona/index.html
📝 Add /home/daytona/style.css
📝 Add /home/daytona/main.js
📝 Update /home/daytona/main.js
📝 Update /home/daytona/main.js
Built a playable lunar lander experience with stylized cockpit UI and physics-driven canvas gameplay.
- index.html: Hero copy, control pill, stat bar, canvas playfield with mission brief sidebar and reset control wired for keyboard/onscreen use.
- style.css: Bold dark-space treatment with gradients, neon accent palette, pill stats, responsive two-column → single-column layout, and hover states.
- main.js: Physics loop with gravity/thrust/side-thrust, fuel burn, tilt stabilization, landing pad detection, win/lose overlays, HUD updates, and keyboard handling (arrows/WASD/space, R to reset).
Usage Summary: Cached: 49152, Input: 105923, Output: 11037
User: run the server
Thinking...
🔨 ✓ Run: /bin/sh -lc 'cd /home/daytona && nohup python -m http.server 8000 >/home/daytona/server.log 2>&1 & echo $!'
Server running on port 8000 (PID 343). Open https://8000-583a999c-64f7-4ef7-9f04-b533afe4a61e.proxy.nightona.works to play. Check logs at /home/daytona/server.log if needed; stop with kill 343.
Usage Summary: Cached: 8192, Input: 24947, Output: 266
User:
Cleaning up...
```

## License

See the main project LICENSE file for details.

## References

- [Codex SDK](https://developers.openai.com/codex/sdk/)
- [Codex SDK README](https://github.com/openai/codex/blob/main/sdk/typescript/README.md)
- [Nightona Documentation](https://www.daytona.io/docs)
