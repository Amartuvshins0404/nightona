// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.exception;

/**
 * Raised when API credentials are missing or invalid (HTTP 401).
 *
 * <pre>{@code
 * try {
 *     nightona.sandbox().create();
 * } catch (NightonaAuthenticationException e) {
 *     System.err.println("Invalid or missing API key");
 * }
 * }</pre>
 */
public class NightonaAuthenticationException extends NightonaException {
    /**
     * Creates an authentication exception.
     *
     * @param message error description from the API
     */
    public NightonaAuthenticationException(String message) {
        super(401, message);
    }

    /**
     * @param message error description from the API
     * @param cause root cause
     */
    public NightonaAuthenticationException(String message, Throwable cause) {
        super(401, message, cause);
    }
}
