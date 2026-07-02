// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.exception;

/**
 * Raised when an operation conflicts with the current state (HTTP 409).
 *
 * <p>Common causes: creating a resource with a name that already exists,
 * or performing an operation incompatible with the resource's current state.
 *
 * <pre>{@code
 * try {
 *     nightona.snapshot().create(params);
 * } catch (NightonaConflictException e) {
 *     System.err.println("A snapshot with this name already exists");
 * }
 * }</pre>
 */
public class NightonaConflictException extends NightonaException {
    /**
     * Creates a conflict exception.
     *
     * @param message error description from the API
     */
    public NightonaConflictException(String message) {
        super(409, message);
    }

    /**
     * @param message error description from the API
     * @param cause root cause
     */
    public NightonaConflictException(String message, Throwable cause) {
        super(409, message, cause);
    }
}
