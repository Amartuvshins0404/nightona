// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.examples;

import io.nightona.sdk.Nightona;
import io.nightona.sdk.Sandbox;

public class AutoDelete {
    public static void main(String[] args) {
        try (Nightona nightona = new Nightona()) {
            Sandbox sandbox = nightona.create();
            try {
                System.out.println("autoDeleteInterval: " + sandbox.getAutoDeleteInterval());

                sandbox.setAutoDeleteInterval(60);
                System.out.println("autoDeleteInterval: " + sandbox.getAutoDeleteInterval());

                sandbox.setAutoDeleteInterval(0);
                System.out.println("autoDeleteInterval: " + sandbox.getAutoDeleteInterval());

                sandbox.setAutoDeleteInterval(-1);
                System.out.println("autoDeleteInterval: " + sandbox.getAutoDeleteInterval());
            } finally {
                System.out.println("Deleting sandbox");
                sandbox.delete();
            }
        }
    }
}
