/*
 * Copyright 2025 Daytona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

import { NightonaConfiguration } from '@nightona/api-client'

export type DashboardConfig = NightonaConfiguration & {
  apiUrl: string
}
