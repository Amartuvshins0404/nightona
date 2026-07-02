// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.exception;

/**
 * Raised when the request is malformed or contains invalid parameters (HTTP 400).
 *
 * <pre>{@code
 * try {
 *     nightona.sandbox().create(params);
 * } catch (NightonaBadRequestException e) {
 *     System.err.println("Invalid request parameters: " + e.getMessage());
 * }
 * }</pre>
 */
public class NightonaBadRequestException extends NightonaException {
    /**
     * Creates a bad-request exception.
     *
     * @param message error description from the API
     */
    public NightonaBadRequestException(String message) {
        super(400, message);
    }

    /**
     * @param message error description from the API
     * @param cause root cause
     */
    public NightonaBadRequestException(String message, Throwable cause) {
        super(400, message, cause);
    }
}
