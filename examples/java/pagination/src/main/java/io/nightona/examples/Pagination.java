// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.examples;

import io.nightona.sdk.Nightona;
import io.nightona.sdk.Sandbox;
import io.nightona.sdk.model.ListSandboxesQuery;
import io.nightona.sdk.model.SandboxListSortDirection;
import io.nightona.sdk.model.SandboxListSortField;
import io.nightona.sdk.model.SandboxState;

import java.util.List;
import java.util.Map;

public class Pagination {
    public static void main(String[] args) {
        try (Nightona nightona = new Nightona()) {
            ListSandboxesQuery query = new ListSandboxesQuery();
            query.setLimit(10);
            query.setLabels(Map.of("env", "dev"));
            query.setStates(List.of(SandboxState.STARTED));
            query.setSort(SandboxListSortField.CREATED_AT);
            query.setOrder(SandboxListSortDirection.DESC);

            for (Sandbox sandbox : nightona.list(query)) {
                System.out.println(sandbox.getId());
            }
        }
    }
}
