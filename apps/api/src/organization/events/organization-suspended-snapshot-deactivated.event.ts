/*
 * Copyright 2025 Nightona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

export class OrganizationSuspendedSnapshotDeactivatedEvent {
  constructor(public readonly snapshotId: string) {}
}
