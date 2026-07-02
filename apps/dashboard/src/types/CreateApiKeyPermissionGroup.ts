/*
 * Copyright 2025 Nightona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

import { CreateApiKeyPermissionsEnum } from '@nightona/api-client'

export interface CreateApiKeyPermissionGroup {
  name: string
  permissions: CreateApiKeyPermissionsEnum[]
}
