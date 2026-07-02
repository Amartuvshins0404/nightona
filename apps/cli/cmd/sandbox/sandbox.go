// Copyright 2025 Daytona Platforms Inc.
// SPDX-License-Identifier: AGPL-3.0

package sandbox

import (
	"github.com/Amartuvshins0404/nightona/apps/cli/internal"
	"github.com/spf13/cobra"
)

var SandboxCmd = &cobra.Command{
	Use:     "sandbox",
	Short:   "Manage Nightona sandboxes",
	Long:    "Commands for managing Nightona sandboxes",
	Aliases: []string{"sandboxes"},
	GroupID: internal.SANDBOX_GROUP,
	Hidden:  true, // Deprecated: use top-level commands instead (e.g., "nightona start" instead of "nightona sandbox start")
}

func init() {
	SandboxCmd.AddCommand(ListCmd)
	SandboxCmd.AddCommand(CreateCmd)
	SandboxCmd.AddCommand(InfoCmd)
	SandboxCmd.AddCommand(DeleteCmd)
	SandboxCmd.AddCommand(StartCmd)
	SandboxCmd.AddCommand(StopCmd)
	SandboxCmd.AddCommand(PauseCmd)
	SandboxCmd.AddCommand(ArchiveCmd)
	SandboxCmd.AddCommand(SSHCmd)
	SandboxCmd.AddCommand(ExecCmd)
	SandboxCmd.AddCommand(PreviewUrlCmd)
}
