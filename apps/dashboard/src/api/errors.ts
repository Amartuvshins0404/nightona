/*
 * Copyright 2025 Daytona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

export class NightonaError extends Error {
  public static fromError(error: Error): NightonaError {
    if (String(error).includes('Organization is suspended')) {
      return new OrganizationSuspendedError(error.message, {
        cause: error.cause,
      })
    }

    return new NightonaError(error.message, {
      cause: error.cause,
    })
  }

  public static fromString(error: string, options?: { cause?: Error }): NightonaError {
    return NightonaError.fromError(new Error(error, options))
  }
}

export class OrganizationSuspendedError extends NightonaError {}
