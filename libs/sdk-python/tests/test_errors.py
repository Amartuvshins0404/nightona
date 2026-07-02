# Copyright Nightona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0

"""Tests for nightona.common.errors module."""

from __future__ import annotations

import pytest

from nightona.common.errors import (
    NightonaError,
    NightonaNotFoundError,
    NightonaRateLimitError,
    NightonaTimeoutError,
    create_nightona_error,
    error_class_from_status_code,
)


class TestNightonaError:
    def test_basic_error(self):
        err = NightonaError("something went wrong")
        assert str(err) == "something went wrong"
        assert err.status_code is None
        assert err.headers == {}

    def test_with_status_code(self):
        err = NightonaError("bad request", status_code=400)
        assert err.status_code == 400
        assert str(err) == "bad request"

    def test_with_headers(self):
        headers = {"X-RateLimit-Remaining": "0", "Retry-After": "60"}
        err = NightonaError("rate limited", status_code=429, headers=headers)
        assert err.status_code == 429
        assert err.headers["X-RateLimit-Remaining"] == "0"
        assert err.headers["Retry-After"] == "60"

    def test_is_exception(self):
        err = NightonaError("test")
        assert isinstance(err, Exception)

    def test_none_headers_becomes_empty_dict(self):
        err = NightonaError("msg", headers=None)
        assert err.headers == {}


class TestNightonaNotFoundError:
    def test_inherits_nightona_error(self):
        err = NightonaNotFoundError("sandbox not found", status_code=404)
        assert isinstance(err, NightonaError)
        assert isinstance(err, Exception)
        assert err.status_code == 404

    def test_message(self):
        err = NightonaNotFoundError("not found")
        assert str(err) == "not found"


class TestNightonaRateLimitError:
    def test_inherits_nightona_error(self):
        err = NightonaRateLimitError("rate limit exceeded", status_code=429)
        assert isinstance(err, NightonaError)
        assert err.status_code == 429

    def test_with_retry_header(self):
        err = NightonaRateLimitError(
            "rate limit",
            status_code=429,
            headers={"Retry-After": "30"},
        )
        assert err.headers["Retry-After"] == "30"


class TestNightonaTimeoutError:
    def test_inherits_nightona_error(self):
        err = NightonaTimeoutError("operation timed out")
        assert isinstance(err, NightonaError)
        assert str(err) == "operation timed out"

    def test_with_status_code(self):
        err = NightonaTimeoutError("timeout", status_code=504)
        assert err.status_code == 504


class TestErrorHierarchy:
    def test_catch_all_with_base_class(self):
        errors = [
            NightonaError("base"),
            NightonaNotFoundError("not found"),
            NightonaRateLimitError("rate limit"),
            NightonaTimeoutError("timeout"),
        ]
        for err in errors:
            with pytest.raises(NightonaError):
                raise err

    def test_specific_catch(self):
        with pytest.raises(NightonaNotFoundError):
            raise NightonaNotFoundError("not found")

        with pytest.raises(NightonaRateLimitError):
            raise NightonaRateLimitError("rate limit")

        with pytest.raises(NightonaTimeoutError):
            raise NightonaTimeoutError("timeout")


class TestErrorFactories:
    def test_error_class_from_status_code(self):
        assert error_class_from_status_code(404) is NightonaNotFoundError
        assert error_class_from_status_code(None) is NightonaError

    def test_create_nightona_error_uses_specific_subclass(self):
        error = create_nightona_error("missing", status_code=404, error_code="NOT_FOUND")

        assert isinstance(error, NightonaNotFoundError)
        assert error.error_code == "NOT_FOUND"
