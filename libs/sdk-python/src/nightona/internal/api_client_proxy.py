# Copyright Daytona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0

from __future__ import annotations

from typing import TypeVar

from nightona_api_client import ApiClient as MainApiClient
from nightona_api_client_async import ApiClient as MainAsyncApiClient
from nightona_toolbox_api_client import ApiClient as ToolboxApiClient
from nightona_toolbox_api_client_async import ApiClient as AsyncToolboxApiClient

# TypeVar constrained to any of the four generated ApiClient types we wrap:
# main (nightona_api_client*) for top-level resources (sandboxes, snapshots, volumes)
# and toolbox (nightona_toolbox_api_client*) for per-sandbox resources (process, fs, etc.).
ApiClientT = TypeVar(
    "ApiClientT",
    MainApiClient,
    MainAsyncApiClient,
    ToolboxApiClient,
    AsyncToolboxApiClient,
)
