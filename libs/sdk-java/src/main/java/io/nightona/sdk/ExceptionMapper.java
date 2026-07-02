// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk;

import io.nightona.sdk.exception.NightonaAuthenticationException;
import io.nightona.sdk.exception.NightonaBadRequestException;
import io.nightona.sdk.exception.NightonaConflictException;
import io.nightona.sdk.exception.NightonaConnectionException;
import io.nightona.sdk.exception.NightonaException;
import io.nightona.sdk.exception.NightonaForbiddenException;
import io.nightona.sdk.exception.NightonaNotFoundException;
import io.nightona.sdk.exception.NightonaRateLimitException;
import io.nightona.sdk.exception.NightonaServerException;
import io.nightona.sdk.exception.NightonaTimeoutException;
import io.nightona.sdk.exception.NightonaValidationException;

import java.net.SocketTimeoutException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class ExceptionMapper {
    private ExceptionMapper() {
    }

    static <T> T callMain(MainSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (io.nightona.api.client.ApiException e) {
            throw map(e.getCode(), e.getResponseBody(), e);
        }
    }

    static void runMain(MainRunnable runnable) {
        try {
            runnable.run();
        } catch (io.nightona.api.client.ApiException e) {
            throw map(e.getCode(), e.getResponseBody(), e);
        }
    }

    static <T> T callToolbox(ToolboxSupplier<T> supplier) {
        try {
            return supplier.get();
        } catch (io.nightona.toolbox.client.ApiException e) {
            throw map(e.getCode(), e.getResponseBody(), e);
        }
    }

    static void runToolbox(ToolboxRunnable runnable) {
        try {
            runnable.run();
        } catch (io.nightona.toolbox.client.ApiException e) {
            throw map(e.getCode(), e.getResponseBody(), e);
        }
    }

    static NightonaException map(int statusCode, String responseBody, Throwable cause) {
        // Only treat status==0 as a transport failure when the ApiException
        // wraps an underlying Throwable; client-side ApiExceptions thrown for
        // parameter validation also have status==0 but no wrapped cause.
        if (statusCode == 0 && (responseBody == null || responseBody.isEmpty())
                && cause != null && cause.getCause() != null) {
            return mapTransportFailure(cause);
        }
        String message = extractMessage(responseBody, statusCode);
        if (statusCode == 0 && (responseBody == null || responseBody.isEmpty())
                && cause != null && cause.getMessage() != null && !cause.getMessage().isEmpty()) {
            message = cause.getMessage();
        }
        switch (statusCode) {
            case 400:
                return new NightonaBadRequestException(message, cause);
            case 401:
                return new NightonaAuthenticationException(message, cause);
            case 403:
                return new NightonaForbiddenException(message, cause);
            case 404:
                return new NightonaNotFoundException(message, cause);
            case 409:
                return new NightonaConflictException(message, cause);
            case 422:
                return new NightonaValidationException(message, cause);
            case 429:
                return new NightonaRateLimitException(message, cause);
            default:
                if (statusCode >= 500) {
                    return new NightonaServerException(statusCode, message, cause);
                }
                return new NightonaException(statusCode, message, cause);
        }
    }

    private static NightonaException mapTransportFailure(Throwable cause) {
        Throwable root = rootCause(cause);
        String message = rootMessage(root);
        if (root instanceof SocketTimeoutException) {
            return new NightonaTimeoutException("Request timed out: " + message, cause);
        }
        return new NightonaConnectionException("Connection failed: " + message, cause);
    }

    private static Throwable rootCause(Throwable t) {
        Throwable current = t;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private static String rootMessage(Throwable t) {
        String msg = t.getMessage();
        if (msg != null && !msg.isEmpty()) {
            return msg;
        }
        return t.getClass().getSimpleName();
    }

    /**
     * Extracts a human-readable message from a raw JSON response body.
     * Looks for a "message" or "error" field; falls back to the raw body or a generic message.
     */
    private static String extractMessage(String responseBody, int statusCode) {
        if (responseBody == null || responseBody.isEmpty()) {
            return "Request failed with status " + statusCode;
        }
        // Try to extract "message" field from JSON
        Matcher messageMatcher = Pattern.compile("\"message\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
                .matcher(responseBody);
        if (messageMatcher.find()) {
            return messageMatcher.group(1);
        }
        // Try to extract "error" field from JSON
        Matcher errorMatcher = Pattern.compile("\"error\"\\s*:\\s*\"((?:[^\"\\\\]|\\\\.)*)\"")
                .matcher(responseBody);
        if (errorMatcher.find()) {
            return errorMatcher.group(1);
        }
        return responseBody;
    }

    @FunctionalInterface
    interface MainSupplier<T> {
        T get() throws io.nightona.api.client.ApiException;
    }

    @FunctionalInterface
    interface MainRunnable {
        void run() throws io.nightona.api.client.ApiException;
    }

    @FunctionalInterface
    interface ToolboxSupplier<T> {
        T get() throws io.nightona.toolbox.client.ApiException;
    }

    @FunctionalInterface
    interface ToolboxRunnable {
        void run() throws io.nightona.toolbox.client.ApiException;
    }
}