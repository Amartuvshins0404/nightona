// Copyright 2025 Daytona Platforms Inc.
// SPDX-License-Identifier: AGPL-3.0

package volume

import (
	"github.com/Amartuvshins0404/nightona/apps/cli/internal"
	"github.com/spf13/cobra"
)

var VolumeCmd = &cobra.Command{
	Use:     "volume",
	Short:   "Manage Nightona volumes",
	Long:    "Commands for managing Nightona volumes",
	Aliases: []string{"volumes"},
	GroupID: internal.SANDBOX_GROUP,
}

func init() {
	VolumeCmd.AddCommand(ListCmd)
	VolumeCmd.AddCommand(CreateCmd)
	VolumeCmd.AddCommand(GetCmd)
	VolumeCmd.AddCommand(DeleteCmd)
}
