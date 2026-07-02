// Copyright 2025 Nightona Platforms Inc.
// SPDX-License-Identifier: AGPL-3.0

package models

import (
	"github.com/Amartuvshins0404/nightona/apps/runner/pkg/models/enums"
)

type BackupInfo struct {
	State    enums.BackupState
	Snapshot string
	Error    error
}
