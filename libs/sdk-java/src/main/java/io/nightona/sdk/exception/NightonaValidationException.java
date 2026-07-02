// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.exception;

/**
 * Raised for semantic validation failures (HTTP 422).
 *
 * <p>Raised when the request is well-formed but the values fail business logic
 * validation (e.g., unsupported resource class, invalid configuration).
 *
 * <pre>{@code
 * try {
 *     nightona.sandbox().create(params);
 * } catch (NightonaValidationException e) {
 *     System.err.println("Validation failed: " + e.getMessage());
 * }
 * }</pre>
 */
public class NightonaValidationException extends NightonaException {
    /**
     * Creates a validation exception.
     *
     * @param message error description from the API
     */
    public NightonaValidationException(String message) {
        super(422, message);
    }

    /**
     * @param message error description from the API
     * @param cause root cause
     */
    public NightonaValidationException(String message, Throwable cause) {
        super(422, message, cause);
    }
}
