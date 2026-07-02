# Copyright 2025 Nightona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0

from __future__ import annotations

from collections.abc import Mapping
from typing import Any


class NightonaError(Exception):
    """Base error for Nightona SDK.

    Example:
        ```python
        try:
            sandbox = nightona.get("missing-sandbox")
        except NightonaError as exc:
            print(exc.status_code)
            print(exc.error_code)
            print(exc.message)
        ```

    Attributes:
        message (str): Error message
        status_code (int | None): HTTP status code if available
        error_code (str | None): Machine-readable error code if available
        headers (dict[str, Any]): Response headers
    """

    def __init__(
        self,
        message: str,
        status_code: int | None = None,
        headers: Mapping[str, Any] | None = None,
        error_code: str | None = None,
    ):
        """Initialize Nightona error.

        Args:
            message (str): Error message
            status_code (int | None): HTTP status code if available
            headers (Mapping[str, Any] | None): Response headers if available
            error_code (str | None): Machine-readable error code if available
        """
        super().__init__(message)
        self.message: str = message
        self.status_code: int | None = status_code
        self.error_code: str | None = error_code
        self.headers: dict[str, Any] = dict(headers or {})


class NightonaNotFoundError(NightonaError):
    """Error for when a resource is not found (HTTP 404).

    Example:
        ```python
        try:
            sandbox.fs.download_file("/workspace/missing.txt")
        except NightonaNotFoundError as exc:
            print(exc.status_code)
        ```
    """


class NightonaAuthenticationError(NightonaError):
    """Error for when authentication fails (HTTP 401).

    Example:
        ```python
        try:
            for sandbox in nightona.list():
                print(sandbox.id)
        except NightonaAuthenticationError as exc:
            print(exc.status_code)
        ```
    """


class NightonaAuthorizationError(NightonaError):
    """Error for when the request is forbidden (HTTP 403).

    Example:
        ```python
        try:
            nightona.get("sandbox-without-access")
        except NightonaAuthorizationError as exc:
            print(exc.message)
        ```
    """


class NightonaRateLimitError(NightonaError):
    """Error for when rate limit is exceeded (HTTP 429).

    Example:
        ```python
        try:
            for sandbox in nightona.list():
                print(sandbox.id)
        except NightonaRateLimitError as exc:
            print(exc.error_code)
        ```
    """


class NightonaConflictError(NightonaError):
    """Error for when a resource conflict occurs (HTTP 409).

    Example:
        ```python
        try:
            params = CreateSandboxFromSnapshotParams(name="existing-sandbox")
            nightona.create(params)
        except NightonaConflictError as exc:
            print(exc.error_code)
        ```
    """


class NightonaValidationError(NightonaError):
    """Error for when input validation fails (HTTP 400 or client-side validation).

    Example:
        ```python
        try:
            Image.debian_slim("3.8")
        except NightonaValidationError as exc:
            print(exc.message)
        ```
    """


class NightonaTimeoutError(NightonaError):
    """Error for when a timeout occurs.

    Example:
        ```python
        try:
            sandbox.wait_for_sandbox_start(timeout=1)
        except NightonaTimeoutError as exc:
            print(exc.message)
        ```
    """


class NightonaConnectionError(NightonaError):
    """Error for when a network connection fails.

    Example:
        ```python
        try:
            pty_handle.wait_for_connection()
        except NightonaConnectionError as exc:
            print(exc.message)
        ```
    """


STATUS_CODE_TO_ERROR: dict[int, type[NightonaError]] = {
    400: NightonaValidationError,
    401: NightonaAuthenticationError,
    403: NightonaAuthorizationError,
    404: NightonaNotFoundError,
    409: NightonaConflictError,
    429: NightonaRateLimitError,
}


def error_class_from_status_code(status_code: int | None) -> type[NightonaError]:
    """Map an HTTP status code to the corresponding NightonaError subclass."""

    if status_code is None:
        return NightonaError

    return STATUS_CODE_TO_ERROR.get(status_code, NightonaError)


def create_nightona_error(
    message: str,
    status_code: int | None = None,
    headers: Mapping[str, Any] | None = None,
    error_code: str | None = None,
) -> NightonaError:
    """Create the appropriate NightonaError subclass from structured error metadata."""

    error_cls = error_class_from_status_code(status_code)
    return error_cls(message, status_code=status_code, headers=headers, error_code=error_code)
