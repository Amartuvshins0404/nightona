// Copyright (c) 2026 Nightona contributors
// SPDX-License-Identifier: Apache-2.0

package nightona

import (
	"os"
	"strings"
)

// envOrLegacy returns the value of the given NIGHTONA_-prefixed environment
// variable. When the NIGHTONA_ variable is unset, it falls back to the value
// of the deprecated DAYTONA_-prefixed twin (e.g. NIGHTONA_API_KEY falls back
// to DAYTONA_API_KEY).
func envOrLegacy(key string) string {
	if value, ok := os.LookupEnv(key); ok {
		return value
	}

	return os.Getenv("DAYTONA_" + strings.TrimPrefix(key, "NIGHTONA_"))
}
