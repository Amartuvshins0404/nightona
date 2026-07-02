/*
 * Copyright 2025 Nightona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

import { Runner } from '../entities/runner.entity'

export class RunnerCreatedEvent {
  constructor(public readonly runner: Runner) {}
}
