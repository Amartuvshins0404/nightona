# Copyright Daytona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0

from __future__ import annotations

import os

from dotenv import dotenv_values


class NightonaEnvReader:
    """Reads NIGHTONA_* env vars on demand without polluting os.environ.

    Parses .env and .env.local once at construction.
    Precedence: runtime env → .env.local → .env
    Within each level, ``NIGHTONA_*`` takes precedence over its legacy ``DAYTONA_*`` twin.
    """

    def __init__(self) -> None:
        self._env_local_vars: dict[str, str] = self._load(".env.local")
        self._env_vars: dict[str, str] = self._load(".env")

    def get(self, name: str) -> str | None:
        if not name.startswith("NIGHTONA_"):
            raise ValueError(f"NightonaEnvReader: variable name must start with 'NIGHTONA_', got '{name}'")
        legacy_name = "DAYTONA_" + name[len("NIGHTONA_") :]
        # 1. Runtime env
        for candidate in (name, legacy_name):
            val = os.environ.get(candidate)
            if val is not None:
                return val
        # 2. .env.local
        for candidate in (name, legacy_name):
            if candidate in self._env_local_vars:
                return self._env_local_vars[candidate]
        # 3. .env
        for candidate in (name, legacy_name):
            if candidate in self._env_vars:
                return self._env_vars[candidate]
        return None

    @staticmethod
    def _load(path: str) -> dict[str, str]:
        parsed = dotenv_values(path)
        return {
            k: v
            for k, v in parsed.items()
            if (k.startswith("NIGHTONA_") or k.startswith("DAYTONA_")) and v is not None
        }
