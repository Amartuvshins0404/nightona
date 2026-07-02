// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.examples;

import io.nightona.sdk.Nightona;
import io.nightona.sdk.NightonaConfig;
import io.nightona.sdk.Sandbox;

public class Region {
    public static void main(String[] args) {
        NightonaConfig config = new NightonaConfig.Builder()
                .apiKey(System.getenv("NIGHTONA_API_KEY"))
                .apiUrl(System.getenv("NIGHTONA_API_URL") != null
                        ? System.getenv("NIGHTONA_API_URL")
                        : "http://localhost:3000/api")
                .target("us")
                .build();

        try (Nightona nightona = new Nightona(config)) {
            System.out.println("Creating sandbox with target: us");
            Sandbox sandbox = nightona.create();
            try {
                System.out.println("Sandbox created: " + sandbox.getId());
                System.out.println("target: " + sandbox.getTarget());
            } finally {
                System.out.println("Deleting sandbox");
                sandbox.delete();
            }
        }
    }
}
