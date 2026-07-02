/**
 * Copyright Nightona Platforms Inc.
 * SPDX-License-Identifier: Apache-2.0
 */

import { z } from 'zod'
import type { PluginInput } from '@opencode-ai/plugin'
import type { ToolContext } from '@opencode-ai/plugin/tool'
import type { NightonaSessionManager } from '../core/session-manager'

export const lspTool = (
  sessionManager: NightonaSessionManager,
  projectId: string,
  worktree: string,
  pluginCtx: PluginInput,
) => ({
  description: 'LSP operation in Nightona sandbox (code intelligence)',
  args: {
    op: z.string(),
    filePath: z.string(),
    line: z.number(),
  },
  async execute(args: { op: string; filePath: string; line: number }, ctx: ToolContext) {
    return `LSP operations are not yet implemented in the Nightona plugin.`
  },
})
