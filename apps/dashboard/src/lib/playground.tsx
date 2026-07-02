/*
 * Copyright 2025 Daytona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

import { ReactNode } from 'react'
import { CodeLanguage } from '@nightona/sdk'

export const createErrorMessageOutput = (error: unknown): ReactNode => {
  return (
    <span>
      <span className="text-red-500">Error: </span>
      <span>{error instanceof Error ? error.message : String(error)}</span>
    </span>
  )
}

export const getLanguageCodeToRun = (language?: CodeLanguage): string => {
  switch (language) {
    case CodeLanguage.TYPESCRIPT:
      return `function greet(name: string): string {
\treturn \`Hello, \${name}!\`;
}
console.log(greet("Nightona"));`
    case CodeLanguage.JAVASCRIPT:
      return `function greet(name) {
\treturn \`Hello, \${name}!\`;
}
console.log(greet("Nightona"));`
    default:
      // Python is default language if none specified
      return `def greet(name):
\treturn f"Hello, {name}!"
print(greet("Nightona"))`
  }
}

export const objectHasAnyValue = (obj: object) => Object.values(obj).some((v) => v !== '' && v !== undefined)
