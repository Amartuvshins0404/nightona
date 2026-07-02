/**
 * Copyright Nightona Platforms Inc.
 * SPDX-License-Identifier: Apache-2.0
 */

import { z } from 'zod'
import type { PluginInput } from '@opencode-ai/plugin'
import type { ToolContext } from '@opencode-ai/plugin/tool'
import type { NightonaSessionManager } from '../core/session-manager'

export const getPreviewURLTool = (
  sessionManager: NightonaSessionManager,
  projectId: string,
  worktree: string,
  pluginCtx: PluginInput,
) => ({
  description: 'Gets a preview URL for the Nightona sandbox',
  args: {
    port: z.number(),
  },
  async execute(args: { port: number }, ctx: ToolContext) {
    const sandbox = await sessionManager.getSandbox(ctx.sessionID, projectId, worktree, pluginCtx)
    const previewLink = await sandbox.getPreviewLink(args.port)
    return `Sandbox Preview URL: ${previewLink.url}`
  },
})
