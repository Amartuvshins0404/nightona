// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.model;

public class Command extends io.nightona.toolbox.client.model.Command {
    public Command() {}

    public Command(io.nightona.toolbox.client.model.Command source) {
        super();
        if (source != null) {
            setId(source.getId());
            setCommand(source.getCommand());
            setExitCode(source.getExitCode());
        }
    }
}
