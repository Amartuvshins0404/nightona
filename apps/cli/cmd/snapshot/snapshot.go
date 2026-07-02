// Copyright 2025 Nightona Platforms Inc.
// SPDX-License-Identifier: AGPL-3.0

package snapshot

import (
	"github.com/Amartuvshins0404/nightona/apps/cli/internal"
	"github.com/spf13/cobra"
)

var SnapshotsCmd = &cobra.Command{
	Use:     "snapshot",
	Short:   "Manage Nightona snapshots",
	Long:    "Commands for managing Nightona snapshots",
	Aliases: []string{"snapshots"},
	GroupID: internal.SANDBOX_GROUP,
}

func init() {
	SnapshotsCmd.AddCommand(ListCmd)
	SnapshotsCmd.AddCommand(CreateCmd)
	SnapshotsCmd.AddCommand(PushCmd)
	SnapshotsCmd.AddCommand(DeleteCmd)
}
