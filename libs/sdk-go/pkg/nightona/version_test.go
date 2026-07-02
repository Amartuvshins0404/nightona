// Copyright Daytona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package nightona

import (
	"strings"
	"testing"

	"github.com/stretchr/testify/assert"
)

func TestVersionIsTrimmedAndPresent(t *testing.T) {
	assert.NotEmpty(t, Version)
	assert.Equal(t, strings.TrimSpace(Version), Version)
}
