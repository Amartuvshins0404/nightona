// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.examples;

import io.nightona.sdk.Nightona;
import io.nightona.sdk.Sandbox;

public class AutoArchive {
    public static void main(String[] args) {
        try (Nightona nightona = new Nightona()) {
            Sandbox sandbox = nightona.create();
            try {
                System.out.println("autoArchiveInterval: " + sandbox.getAutoArchiveInterval());

                sandbox.setAutoArchiveInterval(60);
                System.out.println("autoArchiveInterval: " + sandbox.getAutoArchiveInterval());
            } finally {
                System.out.println("Deleting sandbox");
                sandbox.delete();
            }
        }
    }
}
