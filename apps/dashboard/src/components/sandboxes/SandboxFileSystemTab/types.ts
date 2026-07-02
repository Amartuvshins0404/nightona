/*
 * Copyright Daytona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

import type { Nightona } from '@nightona-co/sdk'

export type PreviewKind = 'binary' | 'image' | 'text'

export type SandboxInstance = Awaited<ReturnType<Nightona['get']>>

export type SandboxFileSystemNode = {
  group: string
  id: string
  isDir: boolean
  modTime: string
  mode: string
  name: string
  owner: string
  path: string
  permissions: string
  size: number
}
