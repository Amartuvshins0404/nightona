// Copyright 2025 Nightona Platforms Inc.
// SPDX-License-Identifier: AGPL-3.0

package cmd

import (
	"fmt"

	"github.com/Amartuvshins0404/nightona/apps/cli/internal"
	"github.com/spf13/cobra"
)

var VersionCmd = &cobra.Command{
	Use:   "version",
	Short: "Print the version number",
	RunE: func(cmd *cobra.Command, args []string) error {
		fmt.Println("Nightona CLI version", internal.Version)
		return nil
	},
}
