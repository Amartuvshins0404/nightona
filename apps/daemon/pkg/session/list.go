// Copyright 2025 Nightona Platforms Inc.
// SPDX-License-Identifier: AGPL-3.0

package session

import "github.com/Amartuvshins0404/nightona/apps/daemon/internal/util"

func (s *SessionService) List() ([]Session, error) {
	sessions := []Session{}

	for _, sessionId := range s.sessions.Keys() {
		if sessionId == util.EntrypointSessionID {
			continue
		}

		commands, err := s.getSessionCommands(sessionId)
		if err != nil {
			return nil, err
		}

		sessions = append(sessions, Session{
			SessionId: sessionId,
			Commands:  commands,
		})
	}

	return sessions, nil
}
