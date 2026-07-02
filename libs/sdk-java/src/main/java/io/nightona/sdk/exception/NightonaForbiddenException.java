// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.exception;

/**
 * Raised when the authenticated user lacks permission to perform an operation (HTTP 403).
 *
 * <pre>{@code
 * try {
 *     nightona.sandbox().delete(sandboxId);
 * } catch (NightonaForbiddenException e) {
 *     System.err.println("Not authorized to delete this sandbox");
 * }
 * }</pre>
 */
public class NightonaForbiddenException extends NightonaException {
    /**
     * Creates a forbidden exception.
     *
     * @param message error description from the API
     */
    public NightonaForbiddenException(String message) {
        super(403, message);
    }

    /**
     * @param message error description from the API
     * @param cause root cause
     */
    public NightonaForbiddenException(String message, Throwable cause) {
        super(403, message, cause);
    }
}
