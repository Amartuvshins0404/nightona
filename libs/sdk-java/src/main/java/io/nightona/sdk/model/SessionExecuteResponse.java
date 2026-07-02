// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.model;

public class SessionExecuteResponse extends io.nightona.toolbox.client.model.SessionExecuteResponse {
    public SessionExecuteResponse() {}

    public SessionExecuteResponse(io.nightona.toolbox.client.model.SessionExecuteResponse source) {
        super();
        if (source != null) {
            setCmdId(source.getCmdId());
            setOutput(source.getOutput());
            setStdout(source.getStdout());
            setStderr(source.getStderr());
            setExitCode(source.getExitCode());
        }
    }
}
