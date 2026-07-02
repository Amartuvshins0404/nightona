// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

import {
  createNightonaError,
  NightonaAuthenticationError,
  NightonaAuthorizationError,
  NightonaConflictError,
  NightonaError,
  NightonaNotFoundError,
  NightonaRateLimitError,
  NightonaTimeoutError,
  NightonaValidationError,
  errorClassFromStatusCode,
} from '../errors/NightonaError'

describe('Nightona errors', () => {
  it('constructs NightonaError with properties', () => {
    const err = new NightonaError('boom', 500)
    expect(err).toBeInstanceOf(Error)
    expect(err.name).toBe('NightonaError')
    expect(err.message).toBe('boom')
    expect(err.statusCode).toBe(500)
  })

  test.each([
    [NightonaNotFoundError, 'NightonaNotFoundError'],
    [NightonaRateLimitError, 'NightonaRateLimitError'],
    [NightonaTimeoutError, 'NightonaTimeoutError'],
  ])('constructs %s', (ErrCtor, expectedName) => {
    const err = new ErrCtor('x', 404)
    expect(err).toBeInstanceOf(NightonaError)
    expect(err.name).toBe(expectedName)
    expect(err.statusCode).toBe(404)
  })

  test.each([
    [400, NightonaValidationError],
    [401, NightonaAuthenticationError],
    [403, NightonaAuthorizationError],
    [404, NightonaNotFoundError],
    [409, NightonaConflictError],
    [429, NightonaRateLimitError],
    [500, NightonaError],
    [undefined, NightonaError],
  ])('maps status %s to the correct error class', (statusCode, ErrCtor) => {
    expect(errorClassFromStatusCode(statusCode)).toBe(ErrCtor)
  })

  it('creates subclassed errors from structured metadata', () => {
    const err = createNightonaError('missing', 404, undefined, 'FILE_NOT_FOUND')

    expect(err).toBeInstanceOf(NightonaNotFoundError)
    expect(err.errorCode).toBe('FILE_NOT_FOUND')
    expect(err.message).toBe('missing')
  })
})
