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
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExceptionMapperTest {

    @Test
    void callMainMapsBadRequest() {
        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> {
            throw new io.nightona.api.client.ApiException(400, "bad", null, "{\"message\":\"invalid\"}");
        })).isInstanceOf(NightonaBadRequestException.class).hasMessage("invalid");
    }

    @Test
    void callMainMapsAuthentication() {
        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> {
            throw new io.nightona.api.client.ApiException(401, "auth", null, "{\"message\":\"denied\"}");
        })).isInstanceOf(NightonaAuthenticationException.class).hasMessage("denied");
    }

    @Test
    void callToolboxMapsForbiddenAndNotFound() {
        assertThatThrownBy(() -> ExceptionMapper.callToolbox(() -> {
            throw new io.nightona.toolbox.client.ApiException(403, "forbidden", null, "{\"error\":\"blocked\"}");
        })).isInstanceOf(NightonaForbiddenException.class).hasMessage("blocked");

        assertThatThrownBy(() -> ExceptionMapper.callToolbox(() -> {
            throw new io.nightona.toolbox.client.ApiException(404, "missing", null, "{\"message\":\"gone\"}");
        })).isInstanceOf(NightonaNotFoundException.class).hasMessage("gone");
    }

    @Test
    void mapsConflictValidationAndRateLimit() {
        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> {
            throw new io.nightona.api.client.ApiException(409, "conflict", null, "{\"message\":\"exists\"}");
        })).isInstanceOf(NightonaConflictException.class).hasMessage("exists");

        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> {
            throw new io.nightona.api.client.ApiException(422, "invalid", null, "{\"message\":\"bad data\"}");
        })).isInstanceOf(NightonaValidationException.class).hasMessage("bad data");

        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> {
            throw new io.nightona.api.client.ApiException(429, "limit", null, "{\"message\":\"too many\"}");
        })).isInstanceOf(NightonaRateLimitException.class).hasMessage("too many");
    }

    @Test
    void mapsServerAndGenericStatuses() {
        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> {
            throw new io.nightona.api.client.ApiException(503, "server", null, "{\"message\":\"retry\"}");
        })).isInstanceOf(NightonaServerException.class).hasMessage("retry");

        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> {
            throw new io.nightona.api.client.ApiException(418, "teapot", null, "raw body");
        })).isInstanceOf(NightonaException.class).satisfies(error -> {
            NightonaException exception = (NightonaException) error;
            assertThat(exception.getStatusCode()).isEqualTo(418);
            assertThat(exception.getMessage()).isEqualTo("raw body");
        });
    }

    @Test
    void usesFallbackMessageWhenBodyMissing() {
        assertThatThrownBy(() -> ExceptionMapper.callToolbox(() -> {
            throw new io.nightona.toolbox.client.ApiException(500, "server", null, null);
        })).isInstanceOf(NightonaServerException.class).hasMessage("Request failed with status 500");
    }

    @Test
    void extractsErrorFieldAndRawBodyWhenMessageMissing() {
        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> {
            throw new io.nightona.api.client.ApiException(404, "missing", null, "{\"error\":\"gone\"}");
        })).isInstanceOf(NightonaNotFoundException.class).hasMessage("gone");

        assertThatThrownBy(() -> ExceptionMapper.callToolbox(() -> {
            throw new io.nightona.toolbox.client.ApiException(418, "teapot", null, "not-json");
        })).isInstanceOf(NightonaException.class).hasMessage("not-json");
    }

    @Test
    void preservesEscapedJsonMessageContent() {
        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> {
            throw new io.nightona.api.client.ApiException(400, "bad", null, "{\"message\":\"invalid \\\"value\\\"\"}");
        })).isInstanceOf(NightonaBadRequestException.class).hasMessage("invalid \\\"value\\\"");
    }

    @Test
    void runHelpersMapApiExceptions() {
        assertThatThrownBy(() -> ExceptionMapper.runMain(() -> {
            throw new io.nightona.api.client.ApiException(409, "conflict", null, "{\"message\":\"exists\"}");
        })).isInstanceOf(NightonaConflictException.class).hasMessage("exists");

        assertThatThrownBy(() -> ExceptionMapper.runToolbox(() -> {
            throw new io.nightona.toolbox.client.ApiException(403, "forbidden", null, "{\"message\":\"blocked\"}");
        })).isInstanceOf(NightonaForbiddenException.class).hasMessage("blocked");
    }

    @Test
    void runHelpersExecuteSuccessfulCallbacks() {
        String value = ExceptionMapper.callMain(() -> "ok");
        ExceptionMapper.runMain(() -> { });
        ExceptionMapper.runToolbox(() -> { });

        assertThat(value).isEqualTo("ok");
    }

    @Test
    void preservesApiExceptionAsCause() {
        io.nightona.api.client.ApiException apiException =
                new io.nightona.api.client.ApiException(404, "not found", null, "{\"message\":\"gone\"}");

        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> { throw apiException; }))
                .isInstanceOf(NightonaNotFoundException.class)
                .hasCause(apiException);
    }

    @Test
    void preservesNestedIoExceptionCauseChain() {
        IOException ioException = new IOException("connection reset");
        io.nightona.api.client.ApiException apiException = new io.nightona.api.client.ApiException(ioException);

        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> { throw apiException; }))
                .hasCause(apiException)
                .hasRootCause(ioException);
    }

    @Test
    void mapsSocketTimeoutToTimeoutException() {
        SocketTimeoutException timeout = new SocketTimeoutException("Read timed out");
        io.nightona.api.client.ApiException apiException = new io.nightona.api.client.ApiException(timeout);

        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> { throw apiException; }))
                .isInstanceOf(NightonaTimeoutException.class)
                .hasMessageContaining("Read timed out")
                .hasCause(apiException);
    }

    @Test
    void mapsConnectExceptionToConnectionException() {
        ConnectException connectException = new ConnectException("Connection refused");
        io.nightona.api.client.ApiException apiException = new io.nightona.api.client.ApiException(connectException);

        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> { throw apiException; }))
                .isInstanceOf(NightonaConnectionException.class)
                .hasMessageContaining("Connection refused")
                .hasCause(apiException);
    }

    @Test
    void mapsGenericIoExceptionToConnectionException() {
        IOException ioException = new IOException("DNS resolution failed");
        io.nightona.api.client.ApiException apiException = new io.nightona.api.client.ApiException(ioException);

        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> { throw apiException; }))
                .isInstanceOf(NightonaConnectionException.class)
                .hasMessageContaining("DNS resolution failed")
                .hasCause(apiException);
    }

    @Test
    void nullCauseDoesNotThrow() {
        NightonaException exception = ExceptionMapper.map(400, "{\"message\":\"bad\"}", null);
        assertThat(exception).isInstanceOf(NightonaBadRequestException.class);
        assertThat(exception.getCause()).isNull();
        assertThat(exception.getMessage()).isEqualTo("bad");
    }

    @Test
    void clientSideValidationApiExceptionIsNotMisclassifiedAsTransportFailure() {
        io.nightona.api.client.ApiException apiException = new io.nightona.api.client.ApiException(
                "Missing the required parameter 'id' when calling getSandbox(Async)");

        assertThatThrownBy(() -> ExceptionMapper.callMain(() -> { throw apiException; }))
                .isInstanceOf(NightonaException.class)
                .isNotInstanceOf(NightonaConnectionException.class)
                .isNotInstanceOf(NightonaTimeoutException.class)
                .hasMessageContaining("Missing the required parameter 'id'")
                .hasCause(apiException);
    }
}
