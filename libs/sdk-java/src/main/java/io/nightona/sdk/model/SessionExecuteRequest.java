// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.model;

public class SessionExecuteRequest extends io.nightona.toolbox.client.model.SessionExecuteRequest {
    public SessionExecuteRequest() {}

    public SessionExecuteRequest(String command, Boolean runAsync) {
        super();
        setCommand(command);
        setRunAsync(runAsync);
    }

    public SessionExecuteRequest(io.nightona.toolbox.client.model.SessionExecuteRequest source) {
        super();
        if (source != null) {
            setCommand(source.getCommand());
            setRunAsync(source.getRunAsync());
        }
    }
}
