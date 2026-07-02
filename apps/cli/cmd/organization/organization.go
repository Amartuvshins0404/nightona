// Copyright 2025 Nightona Platforms Inc.
// SPDX-License-Identifier: AGPL-3.0

package organization

import (
	"errors"

	"github.com/Amartuvshins0404/nightona/apps/cli/config"
	"github.com/Amartuvshins0404/nightona/apps/cli/internal"
	"github.com/spf13/cobra"
)

var OrganizationCmd = &cobra.Command{
	Use:     "organization",
	Short:   "Manage Nightona organizations",
	Long:    "Commands for managing Nightona organizations",
	Aliases: []string{"organizations", "org", "orgs"},
	GroupID: internal.USER_GROUP,
	PersistentPreRunE: func(cmd *cobra.Command, args []string) error {
		if config.IsApiKeyAuth() {
			return errors.New("organization commands are not available when using API key authentication - run `nightona login` to reauthenticate with browser")
		}

		return nil
	},
}

func init() {
	OrganizationCmd.AddCommand(ListCmd)
	OrganizationCmd.AddCommand(CreateCmd)
	OrganizationCmd.AddCommand(UseCmd)
	OrganizationCmd.AddCommand(DeleteCmd)
}
