// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.exception;

/**
 * Raised when a requested resource does not exist (HTTP 404).
 */
public class NightonaNotFoundException extends NightonaException {
    /**
     * Creates a not-found exception.
     *
     * @param message error description from the API
     */
    public NightonaNotFoundException(String message) {
        super(404, message);
    }

    /**
     * @param message error description from the API
     * @param cause root cause
     */
    public NightonaNotFoundException(String message, Throwable cause) {
        super(404, message, cause);
    }
}
