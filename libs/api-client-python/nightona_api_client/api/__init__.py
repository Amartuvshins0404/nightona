from __future__ import annotations

# flake8: noqa

# import apis into api package
import importlib
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from nightona_api_client.api.health_api import HealthApi
    from nightona_api_client.api.admin_api import AdminApi
    from nightona_api_client.api.api_keys_api import ApiKeysApi
    from nightona_api_client.api.audit_api import AuditApi
    from nightona_api_client.api.config_api import ConfigApi
    from nightona_api_client.api.docker_registry_api import DockerRegistryApi
    from nightona_api_client.api.jobs_api import JobsApi
    from nightona_api_client.api.object_storage_api import ObjectStorageApi
    from nightona_api_client.api.organizations_api import OrganizationsApi
    from nightona_api_client.api.preview_api import PreviewApi
    from nightona_api_client.api.regions_api import RegionsApi
    from nightona_api_client.api.runners_api import RunnersApi
    from nightona_api_client.api.sandbox_api import SandboxApi
    from nightona_api_client.api.snapshots_api import SnapshotsApi
    from nightona_api_client.api.toolbox_api import ToolboxApi
    from nightona_api_client.api.users_api import UsersApi
    from nightona_api_client.api.volumes_api import VolumesApi
    from nightona_api_client.api.webhooks_api import WebhooksApi


_DYNAMIC_IMPORTS: dict[str, str] = {
    "HealthApi": "nightona_api_client.api.health_api",
    "AdminApi": "nightona_api_client.api.admin_api",
    "ApiKeysApi": "nightona_api_client.api.api_keys_api",
    "AuditApi": "nightona_api_client.api.audit_api",
    "ConfigApi": "nightona_api_client.api.config_api",
    "DockerRegistryApi": "nightona_api_client.api.docker_registry_api",
    "JobsApi": "nightona_api_client.api.jobs_api",
    "ObjectStorageApi": "nightona_api_client.api.object_storage_api",
    "OrganizationsApi": "nightona_api_client.api.organizations_api",
    "PreviewApi": "nightona_api_client.api.preview_api",
    "RegionsApi": "nightona_api_client.api.regions_api",
    "RunnersApi": "nightona_api_client.api.runners_api",
    "SandboxApi": "nightona_api_client.api.sandbox_api",
    "SnapshotsApi": "nightona_api_client.api.snapshots_api",
    "ToolboxApi": "nightona_api_client.api.toolbox_api",
    "UsersApi": "nightona_api_client.api.users_api",
    "VolumesApi": "nightona_api_client.api.volumes_api",
    "WebhooksApi": "nightona_api_client.api.webhooks_api",

}


def __getattr__(attr_name: str) -> object:
    module_path = _DYNAMIC_IMPORTS.get(attr_name)
    if module_path is None:
        raise AttributeError(f"module {__name__!r} has no attribute {attr_name!r}")
    mod = importlib.import_module(module_path)
    value = getattr(mod, attr_name)
    globals()[attr_name] = value
    return value


def __dir__() -> list[str]:
    return list(_DYNAMIC_IMPORTS.keys())
