/*
 * Copyright 2025 Nightona Platforms Inc.
 * SPDX-License-Identifier: Apache-2.0
 */

/**
 * @module Errors
 */

import { AxiosHeaders } from 'axios'
import type { AxiosError } from 'axios'

export type ResponseHeaders = InstanceType<typeof AxiosHeaders>

/**
 * Base error for Nightona SDK.
 *
 * @example
 * ```ts
 * try {
 *   await nightona.get('missing-sandbox')
 * } catch (error) {
 *   if (error instanceof NightonaError) {
 *     console.log(error.statusCode)
 *     console.log(error.errorCode)
 *     console.log(error.message)
 *   }
 * }
 * ```
 */
export class NightonaError extends Error {
  /** HTTP status code if available */
  public statusCode?: number
  /** Machine-readable error code if available */
  public errorCode?: string
  /** Response headers if available */
  public headers?: ResponseHeaders

  constructor(message: string, statusCode?: number, headers?: ResponseHeaders, errorCode?: string) {
    super(message)
    this.name = new.target.name
    this.statusCode = statusCode
    this.headers = headers
    this.errorCode = errorCode
  }
}

/**
 * Error thrown when a resource is not found (HTTP 404).
 *
 * @example
 * ```ts
 * try {
 *   await sandbox.fs.downloadFile('/workspace/missing.txt')
 * } catch (error) {
 *   if (error instanceof NightonaNotFoundError) {
 *     console.log(error.statusCode)
 *   }
 * }
 * ```
 */
export class NightonaNotFoundError extends NightonaError {}

/**
 * Error thrown when rate limit is exceeded.
 *
 * @example
 * ```ts
 * try {
 *   for await (const sandbox of nightona.list()) {
 *     console.log(sandbox.id)
 *   }
 * } catch (error) {
 *   if (error instanceof NightonaRateLimitError) {
 *     console.log(error.errorCode)
 *   }
 * }
 * ```
 */
export class NightonaRateLimitError extends NightonaError {}

/**
 * Error thrown when authentication fails (HTTP 401).
 *
 * @example
 * ```ts
 * try {
 *   for await (const sandbox of nightona.list()) {
 *     console.log(sandbox.id)
 *   }
 * } catch (error) {
 *   if (error instanceof NightonaAuthenticationError) {
 *     console.log(error.statusCode)
 *   }
 * }
 * ```
 */
export class NightonaAuthenticationError extends NightonaError {}

/**
 * Error thrown when the request is forbidden (HTTP 403).
 *
 * @example
 * ```ts
 * try {
 *   await nightona.get('sandbox-without-access')
 * } catch (error) {
 *   if (error instanceof NightonaAuthorizationError) {
 *     console.log(error.message)
 *   }
 * }
 * ```
 */
export class NightonaAuthorizationError extends NightonaError {}

/**
 * Error thrown when a resource conflict occurs (HTTP 409).
 *
 * @example
 * ```ts
 * try {
 *   await nightona.create({ name: 'existing-sandbox' })
 * } catch (error) {
 *   if (error instanceof NightonaConflictError) {
 *     console.log(error.errorCode)
 *   }
 * }
 * ```
 */
export class NightonaConflictError extends NightonaError {}

/**
 * Error thrown when input validation fails (HTTP 400 or client-side validation).
 *
 * @example
 * ```ts
 * try {
 *   Image.debianSlim('3.8' as never)
 * } catch (error) {
 *   if (error instanceof NightonaValidationError) {
 *     console.log(error.message)
 *   }
 * }
 * ```
 */
export class NightonaValidationError extends NightonaError {}

/**
 * Error thrown when a timeout occurs.
 *
 * @example
 * ```ts
 * try {
 *   await sandbox.waitUntilStarted(1)
 * } catch (error) {
 *   if (error instanceof NightonaTimeoutError) {
 *     console.log(error.message)
 *   }
 * }
 * ```
 */
export class NightonaTimeoutError extends NightonaError {}

/**
 * Error thrown when a network connection fails.
 *
 * @example
 * ```ts
 * try {
 *   await ptyHandle.waitForConnection()
 * } catch (error) {
 *   if (error instanceof NightonaConnectionError) {
 *     console.log(error.message)
 *   }
 * }
 * ```
 */
export class NightonaConnectionError extends NightonaError {}

const STATUS_CODE_TO_ERROR: Record<number, typeof NightonaError> = {
  400: NightonaValidationError,
  401: NightonaAuthenticationError,
  403: NightonaAuthorizationError,
  404: NightonaNotFoundError,
  409: NightonaConflictError,
  429: NightonaRateLimitError,
}

/**
 * Maps an HTTP status code to the corresponding Nightona error class.
 */
export function errorClassFromStatusCode(statusCode?: number): typeof NightonaError {
  if (statusCode === undefined) {
    return NightonaError
  }

  return STATUS_CODE_TO_ERROR[statusCode] || NightonaError
}

/**
 * Creates the appropriate Nightona error subclass from structured error metadata.
 */
export function createNightonaError(
  message: string,
  statusCode?: number,
  headers?: ResponseHeaders,
  errorCode?: string,
): NightonaError {
  const ErrorClass = errorClassFromStatusCode(statusCode)
  return new ErrorClass(message, statusCode, headers, errorCode)
}

function isAxiosTimeoutError(error: AxiosError): boolean {
  return error.code === 'ECONNABORTED' || error.code === 'ETIMEDOUT' || error.message.includes('timeout of')
}

function getAxiosResponseDataObject(error: AxiosError): Record<string, unknown> | undefined {
  if (!error.response?.data || typeof error.response.data !== 'object') {
    return undefined
  }

  return error.response.data as Record<string, unknown>
}

function extractAxiosErrorCode(responseData?: Record<string, unknown>): string | undefined {
  if (typeof responseData?.code === 'string') {
    return responseData.code
  }

  if (typeof responseData?.error_code === 'string') {
    return responseData.error_code
  }

  if (typeof responseData?.error === 'string') {
    return responseData.error
  }

  return undefined
}

function extractAxiosErrorMessage(error: AxiosError): string {
  if (isAxiosTimeoutError(error)) {
    return 'Operation timed out'
  }

  const responseData = getAxiosResponseDataObject(error)
  const responseMessage: unknown = responseData?.message || error.response?.data
  const message: unknown = responseMessage || error.message || String(error)

  if (typeof message === 'object') {
    try {
      return JSON.stringify(message)
    } catch {
      return String(message)
    }
  }

  return String(message)
}

/**
 * Creates the appropriate Nightona error subclass from an Axios error.
 */
export function createAxiosNightonaError(error: AxiosError): NightonaError {
  const message = extractAxiosErrorMessage(error)
  const statusCode = error.response?.status
  const headers = error.response?.headers as ResponseHeaders | undefined
  const responseData = getAxiosResponseDataObject(error)
  const errorCode = extractAxiosErrorCode(responseData)

  if (isAxiosTimeoutError(error)) {
    return new NightonaTimeoutError(message, statusCode, headers, errorCode)
  }

  if (!error.response && (error.request || error.code)) {
    return new NightonaConnectionError(message, statusCode, headers, errorCode)
  }

  return createNightonaError(message, statusCode, headers, errorCode)
}
