// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.examples;

import io.nightona.sdk.Nightona;
import io.nightona.sdk.Sandbox;

public class NetworkSettings {
    public static void main(String[] args) {
        try (Nightona nightona = new Nightona()) {
            System.out.println("Creating sandbox");
            Sandbox sandbox = nightona.create();
            System.out.println("Sandbox created: " + sandbox.getId());

            try {
                System.out.println("id: " + sandbox.getId());
                System.out.println("state: " + sandbox.getState());
            } finally {
                System.out.println("Deleting sandbox");
                sandbox.delete();
            }
        }
    }
}
