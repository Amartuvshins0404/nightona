// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.exception;

/**
 * Raised when an SDK operation times out.
 *
 * <p>This exception is generated client-side and is not tied to a single HTTP status code.
 */
public class NightonaTimeoutException extends NightonaException {
    /**
     * Creates a timeout exception with a cause.
     *
     * @param message timeout description
     * @param cause root cause
     */
    public NightonaTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a timeout exception.
     *
     * @param message timeout description
     */
    public NightonaTimeoutException(String message) {
        super(message);
    }
}
