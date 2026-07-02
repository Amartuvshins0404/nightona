// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.exception;

import java.util.Collections;
import java.util.Map;

/**
 * Base exception for all Nightona SDK errors.
 *
 * <p>Subclasses map to specific HTTP status codes and allow callers to catch
 * precise failure conditions without string-parsing error messages:
 *
 * <pre>{@code
 * try {
 *     Sandbox sandbox = nightona.sandbox().get("nonexistent-id");
 * } catch (NightonaNotFoundException e) {
 *     // sandbox does not exist
 * } catch (NightonaAuthenticationException e) {
 *     // invalid API key
 * } catch (NightonaException e) {
 *     // other SDK error
 * }
 * }</pre>
 */
public class NightonaException extends RuntimeException {
    private final int statusCode;
    private final Map<String, String> headers;

    /**
     * Creates a generic Nightona exception.
     *
     * @param message error description
     */
    public NightonaException(String message) {
        super(message);
        this.statusCode = 0;
        this.headers = Collections.emptyMap();
    }

    /**
     * Creates a generic Nightona exception with a cause.
     *
     * @param message error description
     * @param cause root cause
     */
    public NightonaException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 0;
        this.headers = Collections.emptyMap();
    }

    /**
     * Creates a Nightona exception with explicit HTTP status code.
     *
     * @param statusCode HTTP status code
     * @param message error description
     */
    public NightonaException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
        this.headers = Collections.emptyMap();
    }

    /**
     * Creates a Nightona exception with explicit HTTP status code and a cause.
     *
     * @param statusCode HTTP status code
     * @param message error description
     * @param cause root cause
     */
    public NightonaException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
        this.headers = Collections.emptyMap();
    }

    /**
     * Creates a Nightona exception with HTTP status code and headers.
     *
     * @param statusCode HTTP status code
     * @param message error description
     * @param headers response headers
     */
    public NightonaException(int statusCode, String message, Map<String, String> headers) {
        super(message);
        this.statusCode = statusCode;
        this.headers = headers != null ? Collections.unmodifiableMap(headers) : Collections.emptyMap();
    }

    /** Returns the HTTP status code, or 0 if not applicable. */
    public int getStatusCode() {
        return statusCode;
    }

    /** Returns the HTTP response headers, or an empty map if not available. */
    public Map<String, String> getHeaders() {
        return headers;
    }
}
