/*
 * Copyright 2025 Daytona Platforms Inc.
 * SPDX-License-Identifier: Apache-2.0
 */

import { Nightona, Sandbox } from '@nightona/sdk'
import { FileUpload } from '@nightona/sdk/src/FileSystem'

let nightonaInstance: Nightona | null = null

export const getNightonaClient = () => {
  if (!nightonaInstance) {
    nightonaInstance = new Nightona()
  }
  return nightonaInstance
}

export const getSandboxById = async (sandboxId: string): Promise<Sandbox> => {
  const nightona = getNightonaClient()
  const sandbox = await nightona.get(sandboxId)
  return sandbox
}

export const createFileUploadFormat = (content: string, path: string): FileUpload => {
  return {
    source: Buffer.from(content, 'utf-8'),
    destination: path,
  }
}

// Default working directory for Nightona sandboxes
const DEFAULT_WORKING_DIR = '/home/daytona'

export const normalizeSandboxPath = (path: string): string => {
  // If path already starts with the working directory, return as-is
  if (path.startsWith(DEFAULT_WORKING_DIR)) {
    return path
  }

  // If path starts with ./, remove the dot and treat as relative
  if (path.startsWith('./')) {
    return `${DEFAULT_WORKING_DIR}${path.slice(1)}`
  }

  // If path starts with /, treat it as relative to working directory
  if (path.startsWith('/')) {
    return `${DEFAULT_WORKING_DIR}${path}`
  }

  // For relative paths, prepend working directory
  return `${DEFAULT_WORKING_DIR}/${path}`
}
