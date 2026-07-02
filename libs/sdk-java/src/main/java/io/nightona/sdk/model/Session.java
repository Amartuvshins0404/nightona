// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.model;

public class Session extends io.nightona.toolbox.client.model.Session {
    public Session() {}

    public Session(io.nightona.toolbox.client.model.Session source) {
        super();
        if (source != null) {
            setSessionId(source.getSessionId());
            setCommands(source.getCommands());
        }
    }
}
