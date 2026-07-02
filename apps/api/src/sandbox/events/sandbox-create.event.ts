/*
 * Copyright 2025 Nightona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

import { Sandbox } from '../entities/sandbox.entity'

export class SandboxCreatedEvent {
  constructor(public readonly sandbox: Sandbox) {}
}
