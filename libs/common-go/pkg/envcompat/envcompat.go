// Copyright (c) 2026 Nightona contributors
// SPDX-License-Identifier: Apache-2.0

// Package envcompat provides backward compatibility with the legacy
// DAYTONA_-prefixed environment variables used before the Nightona rebrand.
package envcompat

import (
	"fmt"
	"os"
	"sort"
	"strings"
)

const (
	legacyPrefix = "DAYTONA_"
	newPrefix    = "NIGHTONA_"
)

// ApplyLegacyEnvAliases maps legacy DAYTONA_* environment variables to their
// NIGHTONA_* equivalents. For every DAYTONA_<X>=<v> present in the
// environment, if NIGHTONA_<X> is unset, NIGHTONA_<X> is set to <v>.
// If any legacy variable was applied, a single deprecation warning listing
// the affected variable names is printed to stderr.
//
// Call this first thing in main(), before any configuration is read.
func ApplyLegacyEnvAliases() {
	var applied []string

	for _, kv := range os.Environ() {
		name, value, ok := strings.Cut(kv, "=")
		if !ok || !strings.HasPrefix(name, legacyPrefix) {
			continue
		}

		suffix := strings.TrimPrefix(name, legacyPrefix)
		if suffix == "" {
			continue
		}

		if _, exists := os.LookupEnv(newPrefix + suffix); exists {
			continue
		}

		if os.Setenv(newPrefix+suffix, value) == nil {
			applied = append(applied, name)
		}
	}

	if len(applied) > 0 {
		sort.Strings(applied)
		fmt.Fprintf(os.Stderr, "Warning: DAYTONA_* env vars are deprecated, use NIGHTONA_* instead (mapped: %s)\n", strings.Join(applied, ", "))
	}
}
