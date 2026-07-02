# Copyright Daytona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0

from __future__ import annotations

from unittest.mock import patch

import pytest

from nightona._utils.env import NightonaEnvReader


class TestNightonaEnvReader:
    def test_get_rejects_non_nightona_variable_names(self):
        reader = NightonaEnvReader()

        with pytest.raises(ValueError, match="must start with 'NIGHTONA_'"):
            reader.get("OTHER_VAR")

    def test_runtime_env_takes_precedence(self, monkeypatch):
        monkeypatch.setenv("NIGHTONA_API_KEY", "runtime")

        with patch.object(
            NightonaEnvReader, "_load", side_effect=[{"NIGHTONA_API_KEY": "local"}, {"NIGHTONA_API_KEY": "env"}]
        ):
            reader = NightonaEnvReader()

        assert reader.get("NIGHTONA_API_KEY") == "runtime"

    def test_env_local_takes_precedence_over_env_file(self, monkeypatch):
        monkeypatch.delenv("NIGHTONA_API_KEY", raising=False)

        with patch.object(
            NightonaEnvReader, "_load", side_effect=[{"NIGHTONA_API_KEY": "local"}, {"NIGHTONA_API_KEY": "env"}]
        ):
            reader = NightonaEnvReader()

        assert reader.get("NIGHTONA_API_KEY") == "local"

    def test_get_returns_none_for_missing_variable(self, monkeypatch):
        monkeypatch.delenv("NIGHTONA_API_KEY", raising=False)

        with patch.object(NightonaEnvReader, "_load", side_effect=[{}, {}]):
            reader = NightonaEnvReader()

        assert reader.get("NIGHTONA_API_KEY") is None

    def test_load_filters_non_nightona_and_none_values(self):
        with patch(
            "nightona._utils.env.dotenv_values",
            return_value={"NIGHTONA_API_KEY": "key", "OTHER": "nope", "NIGHTONA_TARGET": None},
        ):
            assert NightonaEnvReader._load(".env") == {"NIGHTONA_API_KEY": "key"}
