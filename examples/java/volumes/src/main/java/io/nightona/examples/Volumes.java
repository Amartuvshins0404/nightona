// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.examples;

import io.nightona.sdk.Nightona;
import io.nightona.sdk.model.Volume;

public class Volumes {
    public static void main(String[] args) {
        try (Nightona nightona = new Nightona()) {
            String volumeName = "test-vol-" + System.currentTimeMillis();
            Volume volume = nightona.volume().create(volumeName);
            try {
                System.out.println("id: " + volume.getId());
                System.out.println("name: " + volume.getName());
                System.out.println("state: " + volume.getState());

                Volume fetched = nightona.volume().getByName(volumeName);
                System.out.println("Fetched volume: " + fetched.getId());
            } finally {
                System.out.println("Deleting volume");
                try {
                    waitUntilDeletable(nightona, volumeName);
                    nightona.volume().delete(volume.getId());
                } catch (Exception e) {
                    System.out.println("Volume cleanup: " + e.getMessage());
                }
            }
        }
    }

    private static void waitUntilDeletable(Nightona nightona, String volumeName) throws InterruptedException {
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 60_000) {
            Volume v = nightona.volume().getByName(volumeName);
            if ("ready".equalsIgnoreCase(v.getState()) || "error".equalsIgnoreCase(v.getState())) {
                return;
            }
            Thread.sleep(1000);
        }
    }
}
