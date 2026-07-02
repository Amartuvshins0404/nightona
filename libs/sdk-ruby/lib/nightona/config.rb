# Copyright Daytona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

require 'dotenv'

module Nightona
  class Config
    API_URL = 'http://localhost:3000/api'

    # API key for authentication with the Nightona API
    #
    # @return [String, nil] Nightona API key
    attr_accessor :api_key

    # JWT token for authentication with the Nightona API
    #
    # @return [String, nil] Nightona JWT token
    attr_accessor :jwt_token

    # URL of the Nightona API
    #
    # @return [String, nil] Nightona API URL
    attr_accessor :api_url

    # Organization ID for authentication with the Nightona API
    #
    # @return [String, nil] Nightona API URL
    attr_accessor :organization_id

    # Target environment for sandboxes
    #
    # @return [String, nil] Nightona target
    attr_accessor :target

    # Enable OpenTelemetry tracing for SDK operations.
    #
    # @return [Boolean, nil]
    attr_accessor :otel_enabled

    # Experimental configuration options
    #
    # @return [Hash, nil] Experimental configuration hash
    attr_accessor :_experimental

    # Initializes a new Nightona::Config object.
    #
    # @param api_key [String, nil] Nightona API key. Defaults to ENV['NIGHTONA_API_KEY'].
    # @param jwt_token [String, nil] Nightona JWT token. Defaults to ENV['NIGHTONA_JWT_TOKEN'].
    # @param api_url [String, nil] Nightona API URL. Defaults to ENV['NIGHTONA_API_URL'] or Nightona::Config::API_URL.
    # @param organization_id [String, nil] Nightona organization ID. Defaults to ENV['NIGHTONA_ORGANIZATION_ID'].
    # @param target [String, nil] Nightona target. Defaults to ENV['NIGHTONA_TARGET'].
    # @param otel_enabled [Boolean, nil] Enable OpenTelemetry tracing for SDK operations.
    # @param _experimental [Hash, nil] Experimental configuration options.
    def initialize( # rubocop:disable Metrics/ParameterLists
      api_key: nil,
      jwt_token: nil,
      api_url: nil,
      organization_id: nil,
      target: nil,
      otel_enabled: nil,
      _experimental: nil
    )
      @env_reader = nightona_env_reader

      @api_key = api_key || @env_reader.call('NIGHTONA_API_KEY')
      @jwt_token = jwt_token || @env_reader.call('NIGHTONA_JWT_TOKEN')
      @api_url = api_url || @env_reader.call('NIGHTONA_API_URL') || API_URL
      @target = target || @env_reader.call('NIGHTONA_TARGET')
      @organization_id = organization_id || @env_reader.call('NIGHTONA_ORGANIZATION_ID')
      @otel_enabled = otel_enabled
      @_experimental = _experimental
    end

    # Reads a NIGHTONA_-prefixed environment variable using the same precedence
    # as the Config initializer: runtime ENV first, then .env.local, then .env.
    # For backwards compatibility, the legacy DAYTONA_-prefixed variable is used
    # as a fallback at each level when the NIGHTONA_ one is not set.
    # Only names starting with NIGHTONA_ are accepted.
    #
    # @param name [String] The environment variable name. Must start with NIGHTONA_.
    # @return [String, nil] The value of the environment variable, or nil if not set.
    # @raise [ArgumentError] If name does not start with NIGHTONA_.
    def read_env(name)
      @env_reader.call(name)
    end

    private

    # Returns a lambda that looks up NIGHTONA_-prefixed env vars without writing to ENV.
    # Files are parsed once; lookups check runtime env first, then .env.local, then .env.
    # Legacy DAYTONA_-prefixed variables are honored as a fallback.
    def nightona_env_reader
      file_vars = {}
      env_file = File.join(Dir.pwd, '.env')
      file_vars.merge!(nightona_filter(Dotenv.parse(env_file))) if File.exist?(env_file)
      env_local_file = File.join(Dir.pwd, '.env.local')
      file_vars.merge!(nightona_filter(Dotenv.parse(env_local_file))) if File.exist?(env_local_file)

      lambda do |name|
        raise ArgumentError, "Variable must start with 'NIGHTONA_', got '#{name}'" unless name.start_with?('NIGHTONA_')

        legacy_name = name.sub(/\ANIGHTONA_/, 'DAYTONA_')
        return ENV[name] if ENV.key?(name)
        return ENV[legacy_name] if ENV.key?(legacy_name)

        file_vars.key?(name) ? file_vars[name] : file_vars[legacy_name]
      end
    end

    def nightona_filter(env_hash)
      env_hash.select { |k, _| k.start_with?('NIGHTONA_') || k.start_with?('DAYTONA_') }
    end
  end
end
