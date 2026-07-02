// Copyright 2025 Daytona Platforms Inc.
// SPDX-License-Identifier: AGPL-3.0

package util

import (
	"github.com/charmbracelet/lipgloss"
	"github.com/Amartuvshins0404/nightona/apps/cli/views/common"
)

const PropertyNameWidth = 16

var PropertyNameStyle = lipgloss.NewStyle().
	Foreground(common.LightGray)

var PropertyValueStyle = lipgloss.NewStyle().
	Foreground(common.Light).
	Bold(true)
