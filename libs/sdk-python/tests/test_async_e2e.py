# Copyright Nightona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0
"""
End-to-end tests for the async SDK (``AsyncNightona``).

Mirrors ``test_e2e.py`` but exercises the asyncio code path.  Run with::

    NIGHTONA_API_KEY=dtn_... NIGHTONA_API_URL=https://app.daytona.io/api \\
        pytest tests/test_async_e2e.py -m e2e

The suite intentionally starts small (lifecycle / list / get / delete plus the
connection-resilience smoke test that previously lived in
``test_conn_resilience.py``).  Add more cases as new async surface area is
introduced or when a regression needs a guard.
"""
from __future__ import annotations

import asyncio
import os
import time
import uuid
from collections import Counter
from collections.abc import AsyncIterator

import pytest
import pytest_asyncio

from nightona import AsyncNightona, CreateSandboxFromSnapshotParams, ListSandboxesQuery
from nightona.common.errors import NightonaConnectionError, NightonaError, NightonaNotFoundError

if not os.getenv("NIGHTONA_API_KEY"):
    raise RuntimeError("NIGHTONA_API_KEY environment variable is required for E2E tests")

# Module-scoped loop is opt-in here (vs. the suite-wide default of function-scoped)
# so the module-scoped ``async_nightona_client`` / ``async_sandbox`` fixtures
# below can outlive a single test function without ``RuntimeError: Session is
# closed``.  The fixture's ``loop_scope`` must match the test marker's.
pytestmark = [pytest.mark.e2e, pytest.mark.asyncio(loop_scope="module")]


# ---------------------------------------------------------------------------
# Fixtures
# ---------------------------------------------------------------------------


@pytest_asyncio.fixture(loop_scope="module", scope="module")
async def async_nightona_client() -> AsyncIterator[AsyncNightona]:
    async with AsyncNightona() as nightona:
        yield nightona


@pytest_asyncio.fixture(loop_scope="module", scope="module")
async def async_sandbox(async_nightona_client: AsyncNightona):
    params = CreateSandboxFromSnapshotParams(language="python")
    sb = await async_nightona_client.create(params, timeout=120)
    try:
        yield sb
    finally:
        try:
            await async_nightona_client.delete(sb)
        except Exception:
            pass


# ===========================================================================
# Sandbox Lifecycle
# ===========================================================================


async def test_async_sandbox_has_valid_id(async_sandbox):
    assert async_sandbox.id, "Sandbox should have a non-empty ID"


async def test_async_sandbox_has_valid_name(async_sandbox):
    assert async_sandbox.name, "Sandbox should have a non-empty name"


async def test_async_sandbox_state_is_started(async_sandbox):
    state = str(getattr(async_sandbox.state, "value", async_sandbox.state)).lower()
    assert state == "started", f"Expected 'started', got {state!r}"


async def test_async_sandbox_has_resource_properties(async_sandbox):
    assert async_sandbox.cpu > 0, f"Expected cpu > 0, got {async_sandbox.cpu}"
    assert async_sandbox.memory > 0, f"Expected memory > 0, got {async_sandbox.memory}"
    assert async_sandbox.disk > 0, f"Expected disk > 0, got {async_sandbox.disk}"


async def test_async_sandbox_refresh_data_preserves_id(async_sandbox):
    old_id = async_sandbox.id
    await async_sandbox.refresh_data()
    assert async_sandbox.id == old_id
    assert async_sandbox.state is not None


# ===========================================================================
# AsyncNightona Client Operations
# ===========================================================================


async def test_async_get_sandbox_by_id(async_nightona_client, async_sandbox):
    fetched = await async_nightona_client.get(async_sandbox.id)
    assert fetched.id == async_sandbox.id
    assert fetched.name == async_sandbox.name


async def test_async_list_sandboxes_contains_created(async_nightona_client, async_sandbox):
    sandboxes = [s async for s in async_nightona_client.list()]
    assert len(sandboxes) > 0
    assert any(
        s.id == async_sandbox.id for s in sandboxes
    ), f"Expected created sandbox {async_sandbox.id} to appear in list"


async def test_async_list_with_pagination(async_nightona_client, async_sandbox):
    yielded = 0
    async for _ in async_nightona_client.list(ListSandboxesQuery(limit=1)):
        yielded += 1
        if yielded >= 1:
            break
    assert yielded >= 1


async def test_async_get_unknown_sandbox_raises_not_found(async_nightona_client):
    name = f"async-e2e-missing-{uuid.uuid4().hex[:12]}"
    with pytest.raises(NightonaNotFoundError):
        await async_nightona_client.get(name)


# ===========================================================================
# Connection Resilience
# ===========================================================================
#
# Hammers ``nightona.get()`` with names that do not exist and verifies that no
# transient connection errors leak through as ``NightonaConnectionError`` or
# the generic ``NightonaError``.  Guards against regressions of the retry
# wrapper installed by ``SharedAiohttpSession``.
#
# Tune at runtime with ``CONN_TEST_CONCURRENCY`` (default 50) and
# ``CONN_TEST_ROUNDS`` (default 200).

_CONCURRENCY = int(os.environ.get("CONN_TEST_CONCURRENCY", "50"))
_ROUNDS = int(os.environ.get("CONN_TEST_ROUNDS", "200"))


async def _get_nonexistent(nightona: AsyncNightona, sem: asyncio.Semaphore) -> str:
    async with sem:
        name = f"conn-test-{uuid.uuid4().hex[:12]}"
        try:
            await nightona.get(name)
            return "unexpected_found"
        except NightonaNotFoundError:
            return "not_found"
        except NightonaConnectionError as e:
            return f"conn_error:{type(e).__name__}:{e}"
        except NightonaError as e:
            return f"nightona_error:{type(e).__name__}:{e}"
        except Exception as e:  # pragma: no cover - last-resort catch-all
            return f"other:{type(e).__name__}:{e}"


async def test_async_concurrent_get_no_connection_errors(async_nightona_client):
    sem = asyncio.Semaphore(_CONCURRENCY)

    t0 = time.monotonic()
    outcomes = await asyncio.gather(*[_get_nonexistent(async_nightona_client, sem) for _ in range(_ROUNDS)])
    elapsed = time.monotonic() - t0

    results: Counter[str] = Counter(o.split(":", 1)[0] for o in outcomes)
    print(f"\n--- Async connection resilience ({_ROUNDS} requests, {_CONCURRENCY} concurrency, {elapsed:.1f}s) ---")
    for k, v in results.most_common():
        print(f"  {k}: {v}")

    leaked = results.get("conn_error", 0) + results.get("nightona_error", 0) + results.get("other", 0)
    assert leaked == 0, f"{leaked} connection/transport errors leaked through. Full results: {dict(results)}"
