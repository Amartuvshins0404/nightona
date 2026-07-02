/*
 * Copyright 2025 Nightona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

import { OrganizationRolePermissionsEnum } from '@nightona/api-client'

export interface OrganizationRolePermissionGroup {
  name: string
  permissions: OrganizationRolePermissionsEnum[]
}
