/**
 * Copyright Nightona Platforms Inc.
 * SPDX-License-Identifier: Apache-2.0
 */

import type { PluginInput } from '@opencode-ai/plugin'
import { createNightonaTools } from '../tools'
import { logger } from '../core/logger'
import type { NightonaSessionManager } from '../core/session-manager'

/**
 * Custom tools for Nightona sandbox: file ops, command execution, search.
 */
export async function customTools(ctx: PluginInput, sessionManager: NightonaSessionManager) {
  logger.info('OpenCode started with Nightona plugin')
  const projectId = ctx.project.id
  const worktree = ctx.project.worktree
  return createNightonaTools(sessionManager, projectId, worktree, ctx)
}
