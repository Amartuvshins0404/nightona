# Copyright Daytona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0

from __future__ import annotations

import json
from unittest.mock import MagicMock, patch

import pytest

from nightona.common.errors import NightonaAuthenticationError, NightonaValidationError
from nightona.common.nightona import CreateSandboxFromImageParams, CreateSandboxFromSnapshotParams, NightonaConfig
from nightona.common.sandbox import Resources

SYNC_MODULE = "nightona._sync.nightona"


def _make_nightona(config=None):
    from nightona._sync.nightona import Nightona
    from nightona_api_client import Configuration

    with (
        patch(f"{SYNC_MODULE}.ApiClient") as mock_api_cls,
        patch(f"{SYNC_MODULE}.ToolboxApiClient") as mock_toolbox_cls,
    ):
        mock_api_instance = MagicMock()
        mock_api_instance.configuration = Configuration(host="https://test.daytona.io/api")
        mock_api_instance.default_headers = {}
        mock_api_instance.user_agent = ""
        mock_api_cls.return_value = mock_api_instance

        mock_toolbox_instance = MagicMock()
        mock_toolbox_instance.default_headers = {}
        mock_toolbox_cls.return_value = mock_toolbox_instance

        return Nightona(config)


class TestNightonaInit:
    def test_init_with_config(self):
        nightona = _make_nightona(NightonaConfig(api_key="test-key", api_url="https://api.test.io", target="us"))
        assert nightona._api_key == "test-key"
        assert nightona._api_url == "https://api.test.io"
        assert nightona._target == "us"

    def test_init_with_env_vars(self, env_with_api_key):
        nightona = _make_nightona()
        assert nightona._api_key == "test-api-key-123"
        assert nightona._api_url == "https://test.daytona.io/api"
        assert nightona._target == "us"

    def test_init_with_jwt(self, env_with_jwt):
        nightona = _make_nightona()
        assert nightona._jwt_token == "test-jwt-token-123"
        assert nightona._organization_id == "test-org-id"

    @patch("nightona._utils.env.dotenv_values", return_value={})
    def test_init_without_credentials_raises(self, _mock_dotenv, monkeypatch):
        for key in [
            "NIGHTONA_API_KEY",
            "NIGHTONA_JWT_TOKEN",
            "NIGHTONA_API_URL",
            "NIGHTONA_TARGET",
            "NIGHTONA_SERVER_URL",
            "NIGHTONA_ORGANIZATION_ID",
        ]:
            monkeypatch.delenv(key, raising=False)

        from nightona._sync.nightona import Nightona

        with pytest.raises(
            NightonaAuthenticationError, match="Authentication credentials not found. Set NIGHTONA_API_KEY"
        ):
            Nightona()

    @patch("nightona._utils.env.dotenv_values", return_value={})
    def test_default_api_url(self, _mock_dotenv, monkeypatch):
        monkeypatch.setenv("NIGHTONA_API_KEY", "key")
        monkeypatch.setenv("NIGHTONA_TARGET", "us")
        monkeypatch.delenv("NIGHTONA_API_URL", raising=False)
        monkeypatch.delenv("NIGHTONA_SERVER_URL", raising=False)
        monkeypatch.delenv("DAYTONA_API_URL", raising=False)
        monkeypatch.delenv("DAYTONA_SERVER_URL", raising=False)
        nightona = _make_nightona()
        assert nightona._api_url == "http://localhost:3000/api"

    @patch("nightona._utils.env.dotenv_values", return_value={})
    def test_env_server_url_warns_when_api_url_missing(self, _mock_dotenv, monkeypatch):
        monkeypatch.setenv("NIGHTONA_API_KEY", "key")
        monkeypatch.setenv("NIGHTONA_TARGET", "us")
        monkeypatch.setenv("NIGHTONA_SERVER_URL", "https://server.daytona.io/api")
        monkeypatch.delenv("NIGHTONA_API_URL", raising=False)

        with pytest.warns(DeprecationWarning, match="NIGHTONA_SERVER_URL"):
            nightona = _make_nightona()

        assert nightona._api_url == "https://server.daytona.io/api"

    def test_jwt_without_organization_id_raises(self):
        with pytest.raises(NightonaAuthenticationError, match="NIGHTONA_ORGANIZATION_ID is required"):
            _make_nightona(NightonaConfig(jwt_token="jwt", api_url="https://api.test.io", target="us"))


class TestNightonaCreateValidation:
    def test_negative_timeout_raises(self, env_with_api_key):
        nightona = _make_nightona()
        with pytest.raises(NightonaValidationError, match="Timeout must be a non-negative number"):
            nightona._create(CreateSandboxFromSnapshotParams(language="python"), timeout=-1)

    def test_negative_auto_stop_raises(self, env_with_api_key):
        nightona = _make_nightona()
        with pytest.raises(NightonaValidationError, match="auto_stop_interval must be a non-negative"):
            nightona._create(CreateSandboxFromSnapshotParams(language="python", auto_stop_interval=-1), timeout=60)

    def test_negative_auto_archive_raises(self, env_with_api_key):
        nightona = _make_nightona()
        with pytest.raises(NightonaValidationError, match="auto_archive_interval must be a non-negative"):
            nightona._create(CreateSandboxFromSnapshotParams(language="python", auto_archive_interval=-1), timeout=60)

    def test_create_defaults_language_and_sets_label(self, env_with_api_key, sandbox_dto):
        from nightona.common.nightona import CODE_TOOLBOX_LANGUAGE_LABEL

        nightona = _make_nightona()
        nightona._sandbox_api = MagicMock()
        nightona._sandbox_api.create_sandbox.return_value = sandbox_dto
        sandbox = nightona.create()

        request = nightona._sandbox_api.create_sandbox.call_args.kwargs["_request_timeout"]
        assert request == 60
        create_request = nightona._sandbox_api.create_sandbox.call_args.args[0]
        assert create_request.labels[CODE_TOOLBOX_LANGUAGE_LABEL] == "python"
        assert sandbox.id == sandbox_dto.id

    def test_create_from_image_sets_resources(self, env_with_api_key, sandbox_dto):
        nightona = _make_nightona()
        nightona._sandbox_api = MagicMock()
        nightona._sandbox_api.create_sandbox.return_value = sandbox_dto
        params = CreateSandboxFromImageParams(image="python:3.12", resources=Resources(cpu=2, memory=4, disk=8, gpu=1))
        nightona.create(params)
        create_request = nightona._sandbox_api.create_sandbox.call_args.args[0]
        assert create_request.cpu == 2
        assert create_request.memory == 4
        assert create_request.disk == 8
        assert create_request.gpu == 1

    def test_create_from_snapshot_sets_snapshot_and_volume_mounts(self, env_with_api_key, sandbox_dto):
        from nightona.common.volume import VolumeMount

        nightona = _make_nightona()
        nightona._sandbox_api = MagicMock()
        nightona._sandbox_api.create_sandbox.return_value = sandbox_dto
        params = CreateSandboxFromSnapshotParams(
            snapshot="snap-1",
            volumes=[VolumeMount(volume_id="vol-1", mount_path="/data", subpath="logs")],
        )

        nightona.create(params)

        create_request = nightona._sandbox_api.create_sandbox.call_args.args[0]
        assert create_request.snapshot == "snap-1"
        assert create_request.volumes[0].volume_id == "vol-1"
        assert create_request.volumes[0].subpath == "logs"


class TestNightonaGetAndList:
    def test_get_empty_id_raises(self, env_with_api_key):
        nightona = _make_nightona()
        with pytest.raises(NightonaValidationError, match="sandbox_id_or_name is required"):
            nightona.get("")

    def test_get_returns_sandbox(self, env_with_api_key, sandbox_dto):
        from nightona._sync.sandbox import Sandbox

        nightona = _make_nightona()
        nightona._sandbox_api = MagicMock()
        nightona._sandbox_api.get_sandbox.return_value = sandbox_dto
        sandbox = nightona.get("test-sandbox-id")
        assert isinstance(sandbox, Sandbox)
        assert sandbox.id == "test-sandbox-id"

    def test_list_returns_iterator(self, env_with_api_key):
        import inspect

        from nightona._sync.nightona import Nightona

        # ``list`` is a generator function — calling it returns an iterator
        # without performing the API request.
        assert inspect.isgeneratorfunction(Nightona.list)

    def test_list_serializes_labels(self, env_with_api_key, sandbox_dto):
        from nightona import ListSandboxesQuery

        response = MagicMock(items=[sandbox_dto], next_cursor=None)
        nightona = _make_nightona()
        nightona._sandbox_api = MagicMock()
        nightona._sandbox_api.list_sandboxes.return_value = response

        sandboxes = list(nightona.list(ListSandboxesQuery(labels={"project": "test"}, limit=10)))

        assert len(sandboxes) == 1
        kwargs = nightona._sandbox_api.list_sandboxes.call_args.kwargs
        assert json.loads(kwargs["labels"]) == {"project": "test"}
        assert kwargs["limit"] == 10
        # cursor is internal; first page fetch passes None
        assert kwargs["cursor"] is None

    def test_list_paginates_via_cursor(self, env_with_api_key, sandbox_dto):
        page1 = MagicMock(items=[sandbox_dto, sandbox_dto], next_cursor="cursor-2")
        page2 = MagicMock(items=[sandbox_dto], next_cursor=None)

        nightona = _make_nightona()
        nightona._sandbox_api = MagicMock()
        nightona._sandbox_api.list_sandboxes.side_effect = [page1, page2]

        sandboxes = list(nightona.list())

        assert len(sandboxes) == 3
        assert nightona._sandbox_api.list_sandboxes.call_count == 2
        # Second call must carry the cursor returned by page 1.
        second_call_kwargs = nightona._sandbox_api.list_sandboxes.call_args_list[1].kwargs
        assert second_call_kwargs["cursor"] == "cursor-2"

    def test_list_early_termination_stops_fetching(self, env_with_api_key, sandbox_dto):
        page1 = MagicMock(items=[sandbox_dto, sandbox_dto], next_cursor="cursor-2")
        # If the iterator advanced past page 1, the mock would yield page2;
        # we assert that does NOT happen.
        page2 = MagicMock(items=[sandbox_dto], next_cursor=None)

        nightona = _make_nightona()
        nightona._sandbox_api = MagicMock()
        nightona._sandbox_api.list_sandboxes.side_effect = [page1, page2]

        first = next(iter(nightona.list()))
        assert first is not None
        # Only page 1 was fetched.
        assert nightona._sandbox_api.list_sandboxes.call_count == 1


class TestNightonaValidateLanguageLabel:
    def test_none_returns_python(self, env_with_api_key):
        from nightona.common.nightona import CodeLanguage

        nightona = _make_nightona()
        assert nightona._validate_language_label(None) == CodeLanguage.PYTHON

    @pytest.mark.parametrize("value", ["python", "typescript", "javascript"])
    def test_valid_language(self, env_with_api_key, value):
        nightona = _make_nightona()
        assert str(nightona._validate_language_label(value)) == value

    def test_invalid_language_raises(self, env_with_api_key):
        from nightona.common.nightona import CODE_TOOLBOX_LANGUAGE_LABEL

        nightona = _make_nightona()
        with pytest.raises(NightonaValidationError, match=f"Invalid {CODE_TOOLBOX_LANGUAGE_LABEL}"):
            nightona._validate_language_label("ruby")
