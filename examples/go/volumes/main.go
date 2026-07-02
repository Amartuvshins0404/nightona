// Copyright 2025 Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package main

import (
	"context"
	"log"

	"github.com/Amartuvshins0404/nightona/libs/sdk-go/pkg/nightona"
)

func main() {
	// Create a new Nightona client using environment variables
	// Set NIGHTONA_API_KEY before running
	client, err := nightona.NewClient()
	if err != nil {
		log.Fatalf("Failed to create client: %v", err)
	}

	ctx := context.Background()

	// Volume operations example
	log.Println("Volume operations example...")
	volumes, err := client.Volume.List(ctx)
	if err != nil {
		log.Fatalf("Failed to list volumes: %v", err)
	}

	log.Printf("Total volumes: %d\n", len(volumes))
	for _, vol := range volumes {
		log.Printf("  - %s (ID: %s)\n", vol.Name, vol.ID)
	}

	log.Println("\n✓ Volume operations completed successfully!")
}
