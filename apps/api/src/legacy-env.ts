/*
 * Copyright (c) 2026 Nightona contributors
 * SPDX-License-Identifier: AGPL-3.0
 */

/**
 * Backward-compatibility shim for legacy DAYTONA_-prefixed environment variables.
 *
 * Nightona is a fork of Daytona, so existing deployments may still export
 * DAYTONA_<X> variables. For every DAYTONA_<X> present in the environment this
 * module copies its value to NIGHTONA_<X>, unless NIGHTONA_<X> is already set
 * (the new name always wins).
 *
 * IMPORTANT: this module must stay the very first import in main.ts so its
 * top-level code runs before any other module (tracing, AppModule, config)
 * reads the environment.
 */

const LEGACY_PREFIX = 'DAYTONA_'
const NEW_PREFIX = 'NIGHTONA_'

const appliedLegacyNames: string[] = []

for (const key of Object.keys(process.env)) {
  if (!key.startsWith(LEGACY_PREFIX)) {
    continue
  }
  const target = NEW_PREFIX + key.slice(LEGACY_PREFIX.length)
  if (process.env[target] === undefined) {
    process.env[target] = process.env[key]
    appliedLegacyNames.push(key)
  }
}

if (appliedLegacyNames.length > 0) {
  // console.warn is used intentionally: no framework code (e.g. the Nest
  // Logger) may be loaded before this shim runs.
  console.warn(
    `[DEPRECATION] Legacy DAYTONA_-prefixed environment variables were detected and mapped to their NIGHTONA_ equivalents: ${appliedLegacyNames.join(
      ', ',
    )}. Rename them to use the NIGHTONA_ prefix; this fallback will be removed in a future release.`,
  )
}

export {}
