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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ExceptionTypesTest {

    @Test
    void baseExceptionStoresStatusAndImmutableHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("x", "1");

        NightonaException exception = new NightonaException(499, "oops", headers);

        assertThat(exception.getStatusCode()).isEqualTo(499);
        assertThat(exception.getHeaders()).containsEntry("x", "1");
        assertThatThrownBy(() -> exception.getHeaders().put("y", "2"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void baseExceptionStoresCause() {
        IllegalStateException cause = new IllegalStateException("boom");

        NightonaException exception = new NightonaException("message", cause);

        assertThat(exception.getStatusCode()).isZero();
        assertThat(exception.getCause()).isSameAs(cause);
    }

    @Test
    void httpExceptionsExposeExpectedStatusCodes() {
        assertThat(new NightonaBadRequestException("bad").getStatusCode()).isEqualTo(400);
        assertThat(new NightonaAuthenticationException("auth").getStatusCode()).isEqualTo(401);
        assertThat(new NightonaForbiddenException("forbidden").getStatusCode()).isEqualTo(403);
        assertThat(new NightonaNotFoundException("missing").getStatusCode()).isEqualTo(404);
        assertThat(new NightonaConflictException("conflict").getStatusCode()).isEqualTo(409);
        assertThat(new NightonaValidationException("invalid").getStatusCode()).isEqualTo(422);
        assertThat(new NightonaRateLimitException("slow down").getStatusCode()).isEqualTo(429);
        assertThat(new NightonaServerException(503, "server").getStatusCode()).isEqualTo(503);
    }

    @Test
    void connectionAndTimeoutExceptionsUseGenericStatusCode() {
        assertThat(new NightonaConnectionException("offline").getStatusCode()).isZero();
        assertThat(new NightonaTimeoutException("late").getStatusCode()).isZero();
    }

    @Test
    void simpleConstructorsExposeMessages() {
        assertThat(new NightonaConnectionException("offline", new RuntimeException("cause")).getCause())
                .hasMessage("cause");
        assertThat(new NightonaTimeoutException("late").getMessage()).isEqualTo("late");
        assertThat(new NightonaException("plain").getHeaders()).isEqualTo(Collections.<String, String>emptyMap());
    }
}
