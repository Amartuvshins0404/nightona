// Copyright 2025 Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package errors

import (
	"encoding/json"
	"fmt"
	"net/http"

	apiclient "github.com/Amartuvshins0404/nightona/libs/api-client-go"
	"github.com/Amartuvshins0404/nightona/libs/toolbox-api-client-go"
)

// NightonaError is the base error type for all Nightona SDK errors
type NightonaError struct {
	Message    string
	StatusCode int
	Headers    http.Header
}

func (e *NightonaError) Error() string {
	if e.StatusCode != 0 {
		return fmt.Sprintf("Nightona error (status %d): %s", e.StatusCode, e.Message)
	}
	return fmt.Sprintf("Nightona error: %s", e.Message)
}

// NewNightonaError creates a new NightonaError
func NewNightonaError(message string, statusCode int, headers http.Header) *NightonaError {
	return &NightonaError{
		Message:    message,
		StatusCode: statusCode,
		Headers:    headers,
	}
}

// NightonaNotFoundError represents a resource not found error (404)
type NightonaNotFoundError struct {
	*NightonaError
}

func (e *NightonaNotFoundError) Error() string {
	return fmt.Sprintf("Resource not found: %s", e.Message)
}

// NewNightonaNotFoundError creates a new NightonaNotFoundError
func NewNightonaNotFoundError(message string, headers http.Header) *NightonaNotFoundError {
	return &NightonaNotFoundError{
		NightonaError: NewNightonaError(message, http.StatusNotFound, headers),
	}
}

// NightonaRateLimitError represents a rate limit error (429)
type NightonaRateLimitError struct {
	*NightonaError
}

func (e *NightonaRateLimitError) Error() string {
	return fmt.Sprintf("Rate limit exceeded: %s", e.Message)
}

// NewNightonaRateLimitError creates a new NightonaRateLimitError
func NewNightonaRateLimitError(message string, headers http.Header) *NightonaRateLimitError {
	return &NightonaRateLimitError{
		NightonaError: NewNightonaError(message, http.StatusTooManyRequests, headers),
	}
}

// NightonaAuthenticationError represents an authentication error (401)
type NightonaAuthenticationError struct {
	*NightonaError
}

func (e *NightonaAuthenticationError) Error() string {
	return fmt.Sprintf("Authentication failed: %s", e.Message)
}

func NewNightonaAuthenticationError(message string, headers http.Header) *NightonaAuthenticationError {
	return &NightonaAuthenticationError{
		NightonaError: NewNightonaError(message, http.StatusUnauthorized, headers),
	}
}

// NightonaForbiddenError represents a forbidden/authorization error (403)
type NightonaForbiddenError struct {
	*NightonaError
}

func (e *NightonaForbiddenError) Error() string {
	return fmt.Sprintf("Forbidden: %s", e.Message)
}

func NewNightonaForbiddenError(message string, headers http.Header) *NightonaForbiddenError {
	return &NightonaForbiddenError{
		NightonaError: NewNightonaError(message, http.StatusForbidden, headers),
	}
}

// NightonaConflictError represents a conflict error (409)
type NightonaConflictError struct {
	*NightonaError
}

func (e *NightonaConflictError) Error() string {
	return fmt.Sprintf("Conflict: %s", e.Message)
}

func NewNightonaConflictError(message string, headers http.Header) *NightonaConflictError {
	return &NightonaConflictError{
		NightonaError: NewNightonaError(message, http.StatusConflict, headers),
	}
}

// NightonaValidationError represents a validation/bad request error (400)
type NightonaValidationError struct {
	*NightonaError
}

func (e *NightonaValidationError) Error() string {
	return fmt.Sprintf("Validation error: %s", e.Message)
}

func NewNightonaValidationError(message string, headers http.Header) *NightonaValidationError {
	return &NightonaValidationError{
		NightonaError: NewNightonaError(message, http.StatusBadRequest, headers),
	}
}

// NightonaServerError represents a server error (5xx)
type NightonaServerError struct {
	*NightonaError
}

func (e *NightonaServerError) Error() string {
	return fmt.Sprintf("Server error: %s", e.Message)
}

func NewNightonaServerError(message string, statusCode int, headers http.Header) *NightonaServerError {
	return &NightonaServerError{
		NightonaError: NewNightonaError(message, statusCode, headers),
	}
}

// NightonaTimeoutError represents a timeout error
type NightonaTimeoutError struct {
	*NightonaError
}

func (e *NightonaTimeoutError) Error() string {
	return fmt.Sprintf("Operation timed out: %s", e.Message)
}

func NewNightonaTimeoutError(message string) *NightonaTimeoutError {
	return &NightonaTimeoutError{
		NightonaError: NewNightonaError(message, 0, nil),
	}
}

// NewNightonaErrorFromBody parses a JSON response body and maps the status code
// to the appropriate SDK error type. Falls back to the raw body as the message.
func NewNightonaErrorFromBody(body []byte, statusCode int, headers http.Header) error {
	var message string

	if len(body) > 0 {
		var errResp struct {
			Message    string `json:"message"`
			Error      string `json:"error"`
			StatusCode int    `json:"statusCode"`
		}
		if json.Unmarshal(body, &errResp) == nil {
			if errResp.Message != "" {
				message = errResp.Message
			} else if errResp.Error != "" {
				message = errResp.Error
			}
			if errResp.StatusCode != 0 {
				statusCode = errResp.StatusCode
			}
		}
		if message == "" {
			message = string(body)
		}
	}

	if message == "" {
		message = "Download failed"
	}

	switch statusCode {
	case http.StatusNotFound:
		return NewNightonaNotFoundError(message, headers)
	case http.StatusTooManyRequests:
		return NewNightonaRateLimitError(message, headers)
	default:
		return NewNightonaError(message, statusCode, headers)
	}
}

// ConvertAPIError converts api-client-go errors to SDK error types
func ConvertAPIError(err error, httpResp *http.Response) error {
	if err == nil {
		return nil
	}

	var message string
	var statusCode int
	var headers http.Header

	if httpResp != nil {
		statusCode = httpResp.StatusCode
		headers = httpResp.Header
	}

	// Try to extract message from GenericOpenAPIError
	if genErr, ok := err.(*apiclient.GenericOpenAPIError); ok {
		body := genErr.Body()
		if len(body) > 0 {
			// Try to parse as JSON
			var errResp struct {
				Message string `json:"message"`
				Error   string `json:"error"`
			}
			if json.Unmarshal(body, &errResp) == nil {
				if errResp.Message != "" {
					message = errResp.Message
				} else if errResp.Error != "" {
					message = errResp.Error
				}
			}

			// Fall back to raw body if no structured message
			if message == "" {
				message = string(body)
			}
		}

		// Fall back to error string if no body
		if message == "" {
			message = genErr.Error()
		}
	} else {
		message = err.Error()
	}

	return mapStatusCodeToError(statusCode, message, headers)
}

// ConvertToolboxError converts toolbox-api-client-go errors to SDK error types
func ConvertToolboxError(err error, httpResp *http.Response) error {
	if err == nil {
		return nil
	}

	var message string
	var statusCode int
	var headers http.Header

	if httpResp != nil {
		statusCode = httpResp.StatusCode
		headers = httpResp.Header
	}

	// Try to extract message from GenericOpenAPIError
	if genErr, ok := err.(*toolbox.GenericOpenAPIError); ok {
		body := genErr.Body()
		if len(body) > 0 {
			// Try to parse as JSON
			var errResp struct {
				Message string `json:"message"`
				Error   string `json:"error"`
			}
			if json.Unmarshal(body, &errResp) == nil {
				if errResp.Message != "" {
					message = errResp.Message
				} else if errResp.Error != "" {
					message = errResp.Error
				}
			}

			// Fall back to raw body if no structured message
			if message == "" {
				message = string(body)
			}
		}

		// Fall back to error string if no body
		if message == "" {
			message = genErr.Error()
		}
	} else {
		message = err.Error()
	}

	return mapStatusCodeToError(statusCode, message, headers)
}

func mapStatusCodeToError(statusCode int, message string, headers http.Header) error {
	switch {
	case statusCode == http.StatusBadRequest:
		return NewNightonaValidationError(message, headers)
	case statusCode == http.StatusUnauthorized:
		return NewNightonaAuthenticationError(message, headers)
	case statusCode == http.StatusForbidden:
		return NewNightonaForbiddenError(message, headers)
	case statusCode == http.StatusNotFound:
		return NewNightonaNotFoundError(message, headers)
	case statusCode == http.StatusConflict:
		return NewNightonaConflictError(message, headers)
	case statusCode == http.StatusTooManyRequests:
		return NewNightonaRateLimitError(message, headers)
	case statusCode >= 500 && statusCode <= 599:
		return NewNightonaServerError(message, statusCode, headers)
	case statusCode == 0:
		return NewNightonaError(message, 0, nil)
	default:
		return NewNightonaError(message, statusCode, headers)
	}
}
