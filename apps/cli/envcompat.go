// Copyright (c) 2026 Nightona contributors
// SPDX-License-Identifier: AGPL-3.0

package main

import (
	"fmt"
	"os"
	"sort"
	"strings"
)

// applyLegacyEnvAliases maps legacy DAYTONA_* environment variables to their
// NIGHTONA_* equivalents. For every DAYTONA_<X>=<v> present in the
// environment, if NIGHTONA_<X> is unset, NIGHTONA_<X> is set to <v>.
// If any legacy variable was applied, a single deprecation warning listing
// the affected variable names is printed to stderr.
//
// This is a private copy of libs/common-go/pkg/envcompat.ApplyLegacyEnvAliases,
// inlined because the CLI module does not depend on libs/common-go.
func applyLegacyEnvAliases() {
	var applied []string

	for _, kv := range os.Environ() {
		name, value, ok := strings.Cut(kv, "=")
		if !ok || !strings.HasPrefix(name, "DAYTONA_") {
			continue
		}

		suffix := strings.TrimPrefix(name, "DAYTONA_")
		if suffix == "" {
			continue
		}

		if _, exists := os.LookupEnv("NIGHTONA_" + suffix); exists {
			continue
		}

		if os.Setenv("NIGHTONA_"+suffix, value) == nil {
			applied = append(applied, name)
		}
	}

	if len(applied) > 0 {
		sort.Strings(applied)
		fmt.Fprintf(os.Stderr, "Warning: DAYTONA_* env vars are deprecated, use NIGHTONA_* instead (mapped: %s)\n", strings.Join(applied, ", "))
	}
}
