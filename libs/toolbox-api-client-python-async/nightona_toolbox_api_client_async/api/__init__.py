from __future__ import annotations

# flake8: noqa

# import apis into api package
import importlib
from typing import TYPE_CHECKING

if TYPE_CHECKING:
    from nightona_toolbox_api_client_async.api.computer_use_api import ComputerUseApi
    from nightona_toolbox_api_client_async.api.file_system_api import FileSystemApi
    from nightona_toolbox_api_client_async.api.git_api import GitApi
    from nightona_toolbox_api_client_async.api.info_api import InfoApi
    from nightona_toolbox_api_client_async.api.interpreter_api import InterpreterApi
    from nightona_toolbox_api_client_async.api.lsp_api import LspApi
    from nightona_toolbox_api_client_async.api.port_api import PortApi
    from nightona_toolbox_api_client_async.api.process_api import ProcessApi
    from nightona_toolbox_api_client_async.api.server_api import ServerApi


_DYNAMIC_IMPORTS: dict[str, str] = {
    "ComputerUseApi": "nightona_toolbox_api_client_async.api.computer_use_api",
    "FileSystemApi": "nightona_toolbox_api_client_async.api.file_system_api",
    "GitApi": "nightona_toolbox_api_client_async.api.git_api",
    "InfoApi": "nightona_toolbox_api_client_async.api.info_api",
    "InterpreterApi": "nightona_toolbox_api_client_async.api.interpreter_api",
    "LspApi": "nightona_toolbox_api_client_async.api.lsp_api",
    "PortApi": "nightona_toolbox_api_client_async.api.port_api",
    "ProcessApi": "nightona_toolbox_api_client_async.api.process_api",
    "ServerApi": "nightona_toolbox_api_client_async.api.server_api",

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
