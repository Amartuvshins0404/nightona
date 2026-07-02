// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk;

import io.nightona.sdk.exception.NightonaException;

/**
 * Supported programming languages for direct code execution in a Sandbox.
 *
 * <p>Python is used as the default language when no explicit language label is set on the
 * Sandbox.
 */
public enum CodeLanguage {
    PYTHON("python"),
    TYPESCRIPT("typescript"),
    JAVASCRIPT("javascript");

    private final String value;

    CodeLanguage(String value) {
        this.value = value;
    }

    /**
     * Returns the wire value used in Nightona labels and API payloads.
     *
     * @return lowercase language identifier
     */
    public String getValue() {
        return value;
    }

    public static CodeLanguage fromValue(String value) {
        for (CodeLanguage lang : values()) {
            if (lang.value.equals(value)) {
                return lang;
            }
        }
        throw new NightonaException("Invalid " + Nightona.CODE_TOOLBOX_LANGUAGE_LABEL + ": " + value + ". Supported languages: python, javascript, typescript");
    }
}
