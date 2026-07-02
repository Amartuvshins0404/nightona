// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.exception;

/**
 * Raised for network-level connection failures (no HTTP response received).
 *
 * <p>Raised when the SDK cannot reach the Nightona API due to network issues
 * such as DNS failure, connection refused, or TLS errors.
 *
 * <pre>{@code
 * try {
 *     nightona.sandbox().create();
 * } catch (NightonaConnectionException e) {
 *     System.err.println("Cannot reach Nightona API: " + e.getMessage());
 * }
 * }</pre>
 */
public class NightonaConnectionException extends NightonaException {
    /**
     * Creates a connection exception.
     *
     * @param message connection failure description
     */
    public NightonaConnectionException(String message) {
        super(message);
    }

    /**
     * Creates a connection exception with a cause.
     *
     * @param message connection failure description
     * @param cause root cause
     */
    public NightonaConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
