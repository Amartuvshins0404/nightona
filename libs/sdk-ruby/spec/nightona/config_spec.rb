# Copyright Daytona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

RSpec.describe Nightona::Config do
  around do |example|
    env_keys = %w[
      NIGHTONA_API_KEY
      NIGHTONA_JWT_TOKEN
      NIGHTONA_API_URL
      NIGHTONA_TARGET
      NIGHTONA_ORGANIZATION_ID
      NIGHTONA_CUSTOM_VAR
      DAYTONA_API_KEY
      DAYTONA_JWT_TOKEN
      DAYTONA_API_URL
      DAYTONA_TARGET
      DAYTONA_ORGANIZATION_ID
      DAYTONA_CUSTOM_VAR
    ]
    saved = env_keys.to_h { |key| [key, ENV.delete(key)] }
    example.run
  ensure
    saved.each { |key, value| value ? ENV[key] = value : ENV.delete(key) }
  end

  describe '#initialize' do
    it 'accepts explicit api_key' do
      config = described_class.new(api_key: 'my-key')

      expect(config.api_key).to eq('my-key')
    end

    it 'accepts explicit jwt_token and organization_id' do
      config = described_class.new(jwt_token: 'jwt-tok', organization_id: 'org-42')

      expect(config.jwt_token).to eq('jwt-tok')
      expect(config.organization_id).to eq('org-42')
    end

    it 'defaults api_url to API_URL constant' do
      config = described_class.new(api_key: 'k')

      expect(config.api_url).to eq(described_class::API_URL)
    end

    it 'reads values from ENV when explicit args are missing' do
      ENV['NIGHTONA_API_KEY'] = 'env-key'
      ENV['NIGHTONA_API_URL'] = 'https://custom.api'
      ENV['NIGHTONA_TARGET'] = 'eu'
      ENV['NIGHTONA_ORGANIZATION_ID'] = 'org-env'

      config = described_class.new

      expect(config.api_key).to eq('env-key')
      expect(config.api_url).to eq('https://custom.api')
      expect(config.target).to eq('eu')
      expect(config.organization_id).to eq('org-env')
    end

    it 'prefers explicit params over ENV' do
      ENV['NIGHTONA_API_KEY'] = 'env-key'

      config = described_class.new(api_key: 'explicit-key')

      expect(config.api_key).to eq('explicit-key')
    end

    it 'falls back to legacy DAYTONA_ ENV variables' do
      ENV['DAYTONA_API_KEY'] = 'legacy-key'
      ENV['DAYTONA_API_URL'] = 'https://legacy.api'
      ENV['DAYTONA_TARGET'] = 'legacy-target'
      ENV['DAYTONA_ORGANIZATION_ID'] = 'org-legacy'

      config = described_class.new

      expect(config.api_key).to eq('legacy-key')
      expect(config.api_url).to eq('https://legacy.api')
      expect(config.target).to eq('legacy-target')
      expect(config.organization_id).to eq('org-legacy')
    end

    it 'prefers NIGHTONA_ ENV variables over legacy DAYTONA_ ones' do
      ENV['NIGHTONA_API_KEY'] = 'new-key'
      ENV['DAYTONA_API_KEY'] = 'legacy-key'

      config = described_class.new

      expect(config.api_key).to eq('new-key')
    end

    it 'reads .env and .env.local without mutating ENV and prefers .env.local', :real_dotenv do
      Dir.mktmpdir do |dir|
        File.write(File.join(dir, '.env'), <<~ENVFILE)
          NIGHTONA_API_KEY=env-file-key
          NIGHTONA_TARGET=us
          NOT_NIGHTONA=ignored
        ENVFILE
        File.write(File.join(dir, '.env.local'), <<~ENVFILE)
          NIGHTONA_API_KEY=env-local-key
          NIGHTONA_API_URL=https://local.api
        ENVFILE

        Dir.chdir(dir) do
          config = described_class.new

          expect(config.api_key).to eq('env-local-key')
          expect(config.target).to eq('us')
          expect(config.api_url).to eq('https://local.api')
          expect(ENV.fetch('NIGHTONA_API_KEY', nil)).to be_nil
        end
      end
    end

    it 'reads legacy DAYTONA_ variables from .env files as a fallback', :real_dotenv do
      Dir.mktmpdir do |dir|
        File.write(File.join(dir, '.env'), <<~ENVFILE)
          DAYTONA_API_KEY=legacy-file-key
          DAYTONA_ORGANIZATION_ID=legacy-file-org
          NIGHTONA_ORGANIZATION_ID=file-org
        ENVFILE

        Dir.chdir(dir) do
          config = described_class.new

          expect(config.api_key).to eq('legacy-file-key')
          expect(config.organization_id).to eq('file-org')
        end
      end
    end

    it 'stores experimental config' do
      config = described_class.new(api_key: 'k', _experimental: { 'otel_enabled' => true })

      expect(config._experimental).to eq({ 'otel_enabled' => true })
    end
  end

  describe '#read_env' do
    it 'returns values for NIGHTONA_-prefixed variables from ENV' do
      ENV['NIGHTONA_CUSTOM_VAR'] = 'hello'
      config = described_class.new(api_key: 'k')

      expect(config.read_env('NIGHTONA_CUSTOM_VAR')).to eq('hello')
    end

    it 'returns nil for unset NIGHTONA_ variables' do
      config = described_class.new(api_key: 'k')

      expect(config.read_env('NIGHTONA_NONEXISTENT')).to be_nil
    end

    it 'falls back to the legacy DAYTONA_ variable when the NIGHTONA_ one is unset' do
      ENV['DAYTONA_CUSTOM_VAR'] = 'legacy-hello'
      config = described_class.new(api_key: 'k')

      expect(config.read_env('NIGHTONA_CUSTOM_VAR')).to eq('legacy-hello')
    end

    it 'raises ArgumentError for non-NIGHTONA_ variable names' do
      config = described_class.new(api_key: 'k')

      expect { config.read_env('OTHER_VAR') }
        .to raise_error(ArgumentError, /Variable must start with 'NIGHTONA_'/)
    end
  end
end
