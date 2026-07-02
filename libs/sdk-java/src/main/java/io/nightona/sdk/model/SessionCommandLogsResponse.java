// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.model;

public class SessionCommandLogsResponse extends io.nightona.toolbox.client.model.SessionCommandLogsResponse {
    public static SessionCommandLogsResponse from(io.nightona.toolbox.client.model.SessionCommandLogsResponse resp) {
        if (resp == null) {
            return new SessionCommandLogsResponse();
        }
        SessionCommandLogsResponse result = new SessionCommandLogsResponse();
        result.setOutput(resp.getOutput());
        result.setStdout(resp.getStdout());
        result.setStderr(resp.getStderr());
        return result;
    }
}
