// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk.model;

public class FileInfo extends io.nightona.toolbox.client.model.FileInfo {
    public FileInfo() {}

    public FileInfo(io.nightona.toolbox.client.model.FileInfo source) {
        super();
        if (source != null) {
            setName(source.getName());
            setSize(source.getSize());
            setMode(source.getMode());
            setModTime(source.getModTime());
            setIsDir(source.getIsDir());
        }
    }
}
