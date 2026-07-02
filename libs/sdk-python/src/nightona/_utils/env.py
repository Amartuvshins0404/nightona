# Copyright Nightona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0

from __future__ import annotations

import os

from dotenv import dotenv_values


class NightonaEnvReader:
    """Reads NIGHTONA_* env vars on demand without polluting os.environ.

    Parses .env and .env.local once at construction.
    Precedence: runtime env → .env.local → .env
    """

    def __init__(self) -> None:
        self._env_local_vars: dict[str, str] = self._load(".env.local")
        self._env_vars: dict[str, str] = self._load(".env")

    def get(self, name: str) -> str | None:
        if not name.startswith("NIGHTONA_"):
            raise ValueError(f"NightonaEnvReader: variable name must start with 'NIGHTONA_', got '{name}'")
        # 1. Runtime env
        val = os.environ.get(name)
        if val is not None:
            return val
        # 2. .env.local
        if name in self._env_local_vars:
            return self._env_local_vars[name]
        # 3. .env
        return self._env_vars.get(name)

    @staticmethod
    def _load(path: str) -> dict[str, str]:
        parsed = dotenv_values(path)
        return {k: v for k, v in parsed.items() if k.startswith("NIGHTONA_") and v is not None}
