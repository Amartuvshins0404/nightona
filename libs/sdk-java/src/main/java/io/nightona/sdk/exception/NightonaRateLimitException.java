// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.exception;

/**
 * Raised when API rate limits are exceeded (HTTP 429).
 */
public class NightonaRateLimitException extends NightonaException {
    /**
     * Creates a rate-limit exception.
     *
     * @param message error description from the API
     */
    public NightonaRateLimitException(String message) {
        super(429, message);
    }

    /**
     * @param message error description from the API
     * @param cause root cause
     */
    public NightonaRateLimitException(String message, Throwable cause) {
        super(429, message, cause);
    }
}
