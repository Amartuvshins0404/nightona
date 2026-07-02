/*
 * Copyright Nightona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

package models

import "github.com/Amartuvshins0404/nightona/apps/runner/pkg/models/enums"

type SandboxInfo struct {
	SandboxState      enums.SandboxState
	BackupState       enums.BackupState
	BackupSnapshot    string
	BackupErrorReason *string
}
