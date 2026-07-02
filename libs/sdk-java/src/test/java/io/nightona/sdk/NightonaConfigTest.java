// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk;

import io.nightona.sdk.exception.NightonaException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NightonaConfigTest {

    @Test
    void builderStoresExplicitValues() {
        NightonaConfig config = new NightonaConfig.Builder()
                .apiKey("key")
                .apiUrl("https://custom/api")
                .target("us")
                .build();

        assertThat(config.getApiKey()).isEqualTo("key");
        assertThat(config.getApiUrl()).isEqualTo("https://custom/api");
        assertThat(config.getTarget()).isEqualTo("us");
    }

    @Test
    void builderUsesDefaultApiUrlWhenNull() {
        NightonaConfig config = new NightonaConfig.Builder()
                .apiKey("key")
                .apiUrl(null)
                .build();

        assertThat(config.getApiUrl()).isEqualTo("https://app.daytona.io/api");
    }

    @Test
    void builderUsesDefaultApiUrlWhenEmpty() {
        NightonaConfig config = new NightonaConfig.Builder()
                .apiKey("key")
                .apiUrl("")
                .build();

        assertThat(config.getApiUrl()).isEqualTo("https://app.daytona.io/api");
    }

    @Test
    void builderAllowsNullTargetAndApiKey() {
        NightonaConfig config = new NightonaConfig.Builder().build();

        assertThat(config.getApiKey()).isNull();
        assertThat(config.getTarget()).isNull();
        assertThat(config.getApiUrl()).isEqualTo("https://app.daytona.io/api");
    }

    @Test
    void defaultNightonaConstructorReadsEnvironmentVariables() throws Exception {
        Map<String, String> env = new HashMap<String, String>();
        env.put("NIGHTONA_API_KEY", "env-key");
        env.put("NIGHTONA_API_URL", "https://env.example/api/");
        env.put("NIGHTONA_TARGET", "eu");

        TestSupport.withEnvironment(env, () -> {
            try (Nightona nightona = new Nightona()) {
                NightonaConfig config = TestSupport.getField(nightona, "config", NightonaConfig.class);
                assertThat(config.getApiKey()).isEqualTo("env-key");
                assertThat(config.getApiUrl()).isEqualTo("https://env.example/api/");
                assertThat(config.getTarget()).isEqualTo("eu");
            }
        });
    }

    @Test
    void defaultNightonaConstructorFallsBackToDefaultApiUrl() throws Exception {
        Map<String, String> env = new HashMap<String, String>();
        env.put("NIGHTONA_API_KEY", "env-key");
        env.put("NIGHTONA_API_URL", null);
        env.put("NIGHTONA_TARGET", null);

        TestSupport.withEnvironment(env, () -> {
            try (Nightona nightona = new Nightona()) {
                NightonaConfig config = TestSupport.getField(nightona, "config", NightonaConfig.class);
                assertThat(config.getApiUrl()).isEqualTo("https://app.daytona.io/api");
                assertThat(config.getTarget()).isNull();
            }
        });
    }

    @Test
    void defaultNightonaConstructorUsesFallbackWhenApiUrlEnvIsEmpty() throws Exception {
        Map<String, String> env = new HashMap<String, String>();
        env.put("NIGHTONA_API_KEY", "env-key");
        env.put("NIGHTONA_API_URL", "");

        TestSupport.withEnvironment(env, () -> {
            try (Nightona nightona = new Nightona()) {
                NightonaConfig config = TestSupport.getField(nightona, "config", NightonaConfig.class);
                assertThat(config.getApiUrl()).isEqualTo("https://app.daytona.io/api");
            }
        });
    }

    @Test
    void defaultNightonaConstructorRequiresApiKey() throws Exception {
        Map<String, String> env = new HashMap<String, String>();
        env.put("NIGHTONA_API_KEY", null);
        env.put("NIGHTONA_API_URL", null);
        env.put("NIGHTONA_TARGET", null);

        TestSupport.withEnvironment(env, () -> assertThatThrownBy(Nightona::new)
                .isInstanceOf(NightonaException.class)
                .hasMessage("Authentication required: set NIGHTONA_API_KEY environment variable or pass apiKey in NightonaConfig"));
    }
}
