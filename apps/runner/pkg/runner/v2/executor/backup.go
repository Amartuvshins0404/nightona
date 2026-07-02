/*
 * Copyright 2025 Nightona Platforms Inc.
 * SPDX-License-Identifier: AGPL-3.0
 */

package executor

import (
	"context"
	"fmt"

	apiclient "github.com/Amartuvshins0404/nightona/libs/api-client-go"
	"github.com/Amartuvshins0404/nightona/apps/runner/pkg/api/dto"
	"github.com/Amartuvshins0404/nightona/apps/runner/pkg/common"
)

func (e *Executor) createBackup(ctx context.Context, job *apiclient.Job) (any, error) {
	var createBackupDto dto.CreateBackupDTO
	err := e.parsePayload(job.Payload, &createBackupDto)
	if err != nil {
		return nil, fmt.Errorf("failed to unmarshal payload: %w", err)
	}

	// TODO: is state cache needed?
	if err := e.docker.CreateBackup(ctx, job.ResourceId, createBackupDto); err != nil {
		return nil, common.FormatRecoverableError(err)
	}
	return nil, nil
}
