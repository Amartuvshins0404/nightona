// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk;

import io.nightona.sdk.CodeInterpreter.ExecutionError;
import io.nightona.sdk.CodeInterpreter.ExecutionResult;
import io.nightona.sdk.exception.NightonaBadRequestException;
import io.nightona.sdk.exception.NightonaForbiddenException;
import io.nightona.sdk.exception.NightonaException;
import io.nightona.sdk.exception.NightonaNotFoundException;
import io.nightona.sdk.exception.NightonaRateLimitException;
import io.nightona.sdk.exception.NightonaServerException;
import io.nightona.sdk.exception.NightonaTimeoutException;
import io.nightona.toolbox.client.api.InterpreterApi;
import io.nightona.toolbox.client.model.InterpreterContext;
import io.nightona.toolbox.client.model.ListContextsResponse;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeInterpreterTest {

    @Mock
    private InterpreterApi interpreterApi;

    private CodeInterpreter codeInterpreter;

    @BeforeEach
    void setUp() {
        codeInterpreter = new CodeInterpreter(interpreterApi, TestSupport.mockSandbox("http://127.0.0.1:1/toolbox"));
    }

    @Test
    void createContextBuildsRequest() {
        InterpreterContext context = new InterpreterContext();
        context.setId("ctx-1");
        when(interpreterApi.createInterpreterContext(any())).thenReturn(context);

        InterpreterContext created = codeInterpreter.createContext("/workspace");

        assertThat(created.getId()).isEqualTo("ctx-1");
        ArgumentCaptor<io.nightona.toolbox.client.model.CreateContextRequest> captor = ArgumentCaptor.forClass(io.nightona.toolbox.client.model.CreateContextRequest.class);
        verify(interpreterApi).createInterpreterContext(captor.capture());
        assertThat(captor.getValue().getCwd()).isEqualTo("/workspace");
    }

    @Test
    void createContextAllowsNullWorkingDirectory() {
        when(interpreterApi.createInterpreterContext(any())).thenReturn(new InterpreterContext().id("ctx-2"));

        codeInterpreter.createContext();

        ArgumentCaptor<io.nightona.toolbox.client.model.CreateContextRequest> captor = ArgumentCaptor.forClass(io.nightona.toolbox.client.model.CreateContextRequest.class);
        verify(interpreterApi).createInterpreterContext(captor.capture());
        assertThat(captor.getValue().getCwd()).isNull();
    }

    @Test
    void listContextsAndDeleteDelegate() {
        InterpreterContext context = new InterpreterContext();
        context.setId("ctx-1");
        when(interpreterApi.listInterpreterContexts()).thenReturn(new ListContextsResponse().contexts(Collections.singletonList(context)));

        assertThat(codeInterpreter.listContexts()).singleElement().extracting(InterpreterContext::getId).isEqualTo("ctx-1");
        when(interpreterApi.listInterpreterContexts()).thenReturn(null);
        assertThat(codeInterpreter.listContexts()).isEmpty();

        codeInterpreter.deleteContext("ctx-1");
        verify(interpreterApi).deleteInterpreterContext("ctx-1");
    }

    @Test
    void listContextsReturnsEmptyWhenResponseHasNullContexts() {
        when(interpreterApi.listInterpreterContexts()).thenReturn(new ListContextsResponse());

        assertThat(codeInterpreter.listContexts()).isEmpty();
    }

    @Test
    void runCodeAggregatesStreamedMessages() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            List<String> requests = new ArrayList<String>();
            server.enqueue(new MockResponse().withWebSocketUpgrade(new WebSocketListener() {
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    requests.add(text);
                    webSocket.send("{\"type\":\"stdout\",\"text\":\"hello\"}");
                    webSocket.send("{\"type\":\"stderr\",\"text\":\"warn\"}");
                    webSocket.send("{\"type\":\"error\",\"name\":\"ValueError\",\"value\":\"boom\",\"traceback\":\"tb\"}");
                    webSocket.send("{\"type\":\"control\",\"text\":\"completed\"}");
                    webSocket.close(1000, "done");
                }
            }));

            CodeInterpreter interpreter = new CodeInterpreter(interpreterApi, TestSupport.mockSandbox(server.url("/sandbox").toString()));
            List<String> stdout = new ArrayList<String>();
            List<String> stderr = new ArrayList<String>();
            List<ExecutionError> errors = new ArrayList<ExecutionError>();

            ExecutionResult result = interpreter.runCode("print('hi')", new RunCodeOptions()
                    .setTimeout(12)
                    .setOnStdout(stdout::add)
                    .setOnStderr(stderr::add)
                    .setOnError(errors::add));

            assertThat(result.getStdout()).isEqualTo("hello");
            assertThat(result.getStderr()).isEqualTo("warn");
            assertThat(result.getError()).extracting(ExecutionError::getName, ExecutionError::getValue, ExecutionError::getTraceback)
                    .containsExactly("ValueError", "boom", "tb");
            assertThat(stdout).containsExactly("hello");
            assertThat(stderr).containsExactly("warn");
            assertThat(errors).hasSize(1);
            assertThat(requests.get(0)).contains("\"code\":\"print('hi')\"").contains("\"timeout\":12");
        }
    }

    @Test
    void runCodeSendsOnlyCodeWhenOptionsAreNull() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            List<String> requests = new ArrayList<String>();
            server.enqueue(new MockResponse().withWebSocketUpgrade(new WebSocketListener() {
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    requests.add(text);
                    webSocket.send("{\"type\":\"control\",\"text\":\"completed\"}");
                    webSocket.close(1000, "done");
                }
            }));

            CodeInterpreter interpreter = new CodeInterpreter(interpreterApi, TestSupport.mockSandbox(server.url("/sandbox").toString()));
            ExecutionResult result = interpreter.runCode("print('simple')");

            assertThat(result.getStdout()).isEmpty();
            assertThat(requests.get(0)).contains("\"code\":\"print('simple')\"").doesNotContain("timeout");
        }
    }

    @Test
    void runCodeRejectsBlankCode() {
        assertThatThrownBy(() -> codeInterpreter.runCode("  "))
                .isInstanceOf(NightonaException.class)
                .hasMessageContaining("Code is required for execution");
    }

    @Test
    void runCodeMapsTimeoutCloseCode() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().withWebSocketUpgrade(new WebSocketListener() {
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    webSocket.close(4008, "timeout");
                }
            }));

            CodeInterpreter interpreter = new CodeInterpreter(interpreterApi, TestSupport.mockSandbox(server.url("/sandbox").toString()));

            assertThatThrownBy(() -> interpreter.runCode("print('hi')"))
                    .isInstanceOf(NightonaTimeoutException.class)
                    .hasMessageContaining("Execution timed out");
        }
    }

    @Test
    void runCodeMapsUnexpectedCloseCode() throws Exception {
        try (MockWebServer server = new MockWebServer()) {
            server.enqueue(new MockResponse().withWebSocketUpgrade(new WebSocketListener() {
                @Override
                public void onMessage(WebSocket webSocket, String text) {
                    webSocket.close(1011, "boom");
                }
            }));

            CodeInterpreter interpreter = new CodeInterpreter(interpreterApi, TestSupport.mockSandbox(server.url("/sandbox").toString()));

            assertThatThrownBy(() -> interpreter.runCode("print('hi')"))
                    .isInstanceOf(NightonaException.class)
                    .hasMessageContaining("WebSocket closed with code 1011: boom");
        }
    }

    @Test
    void runCodeMapsSocketFailures() {
        CodeInterpreter interpreter = new CodeInterpreter(interpreterApi, TestSupport.mockSandbox("http://127.0.0.1:1/toolbox"));

        assertThatThrownBy(() -> interpreter.runCode("print('hi')"))
                .isInstanceOf(NightonaException.class)
                .hasMessageContaining("Failed to execute code");
    }

    @Test
    void runCodeRejectsMissingToolboxBaseUrl() {
        CodeInterpreter interpreter = new CodeInterpreter(interpreterApi, TestSupport.mockSandbox(""));

        assertThatThrownBy(() -> interpreter.runCode("print('hi')"))
                .isInstanceOf(NightonaException.class)
                .hasMessageContaining("Toolbox base URL is not available");
    }

    @ParameterizedTest
    @MethodSource("mappedToolboxExceptions")
    void createContextMapsToolboxErrors(int status, Class<? extends RuntimeException> type) {
        when(interpreterApi.createInterpreterContext(any()))
                .thenThrow(new io.nightona.toolbox.client.ApiException(status, "boom", null, "{\"message\":\"mapped\"}"));

        assertThatThrownBy(() -> codeInterpreter.createContext("/workspace"))
                .isInstanceOf(type)
                .hasMessage("mapped");
    }

    private static Stream<Arguments> mappedToolboxExceptions() {
        return Stream.of(
                Arguments.of(400, NightonaBadRequestException.class),
                Arguments.of(403, NightonaForbiddenException.class),
                Arguments.of(404, NightonaNotFoundException.class),
                Arguments.of(429, NightonaRateLimitException.class),
                Arguments.of(500, NightonaServerException.class)
        );
    }
}
