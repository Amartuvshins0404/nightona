/*
 * Copyright 2025 Nightona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

export function getStateChangeLockKey(id: string): string {
  return `sandbox:${id}:state-change`
}
