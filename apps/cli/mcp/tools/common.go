// Copyright 2025 Nightona Platforms Inc.
// SPDX-License-Identifier: AGPL-3.0

package tools

import "github.com/Amartuvshins0404/nightona/apps/cli/apiclient"

var nightonaMCPHeaders map[string]string = map[string]string{
	apiclient.NightonaSourceHeader: "nightona-mcp",
}
