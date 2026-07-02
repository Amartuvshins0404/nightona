// Copyright 2025 Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package nightona

import (
	_ "embed"
	"strings"
)

//go:embed VERSION
var version string

// Version is the semantic version of the Nightona SDK.
//
// This value is embedded at build time from the VERSION file.
//
// Example:
//
//	fmt.Printf("Nightona SDK version: %s\n", nightona.Version)
var Version = strings.TrimSpace(version)
