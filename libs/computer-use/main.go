// Copyright 2025 Daytona Platforms Inc.
// SPDX-License-Identifier: AGPL-3.0

package main

import (
	"os"

	cu "github.com/Amartuvshins0404/nightona/libs/computer-use/pkg/computeruse"
	"github.com/Amartuvshins0404/nightona/apps/daemon/pkg/toolbox/computeruse"
	"github.com/Amartuvshins0404/nightona/apps/daemon/pkg/toolbox/computeruse/manager"
	"github.com/hashicorp/go-hclog"
	hc_plugin "github.com/hashicorp/go-plugin"
)

func main() {
	logger := hclog.New(&hclog.LoggerOptions{
		Level:      hclog.Trace,
		Output:     os.Stderr,
		JSONFormat: true,
	})
	hc_plugin.Serve(&hc_plugin.ServeConfig{
		HandshakeConfig: manager.ComputerUseHandshakeConfig,
		Plugins: map[string]hc_plugin.Plugin{
			"nightona-computer-use": &computeruse.ComputerUsePlugin{Impl: &cu.ComputerUse{}},
		},
		Logger: logger,
	})
}
