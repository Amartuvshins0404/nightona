/*
 * Copyright 2025 Nightona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

import { EntityManager } from 'typeorm'

export class UserEmailVerifiedEvent {
  constructor(
    public readonly entityManager: EntityManager,
    public readonly userId: string,
  ) {}
}
