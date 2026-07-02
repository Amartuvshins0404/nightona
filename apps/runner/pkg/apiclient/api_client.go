// Copyright 2025 Daytona Platforms Inc.
// SPDX-License-Identifier: AGPL-3.0

package apiclient

import (
	"net/http"

	apiclient "github.com/Amartuvshins0404/nightona/libs/api-client-go"
	"github.com/Amartuvshins0404/nightona/apps/runner/cmd/runner/config"
	"go.opentelemetry.io/contrib/instrumentation/net/http/otelhttp"
)

var apiClient *apiclient.APIClient

const NightonaSourceHeader = "X-Nightona-Source"

func GetApiClient() (*apiclient.APIClient, error) {
	c, err := config.GetConfig()
	if err != nil {
		return nil, err
	}

	var newApiClient *apiclient.APIClient

	serverUrl := c.NightonaApiUrl

	clientConfig := apiclient.NewConfiguration()
	clientConfig.Servers = apiclient.ServerConfigurations{
		{
			URL: serverUrl,
		},
	}

	clientConfig.AddDefaultHeader("Authorization", "Bearer "+c.ApiToken)

	clientConfig.AddDefaultHeader(NightonaSourceHeader, "runner")

	newApiClient = apiclient.NewAPIClient(clientConfig)

	newApiClient.GetConfig().HTTPClient = &http.Client{
		Transport: otelhttp.NewTransport(http.DefaultTransport),
	}

	apiClient = newApiClient
	return apiClient, nil
}
