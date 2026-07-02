/*
 * Copyright 2025 Nightona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

import { Sandbox } from '../entities/sandbox.entity'

export class SandboxDestroyedEvent {
  constructor(public readonly sandbox: Sandbox) {}
}
