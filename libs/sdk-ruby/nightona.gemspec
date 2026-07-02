# frozen_string_literal: true

require_relative 'lib/nightona/sdk/version'

Gem::Specification.new do |spec|
  spec.name = 'nightona'
  spec.version = Nightona::Sdk::VERSION
  spec.authors = ['Daytona Platforms Inc.', 'Nightona contributors']
  spec.email = ['support@daytona.io', 'amaraaamka0404@gmail.com']

  spec.summary = 'Ruby SDK for Nightona'
  spec.description = 'High-level Ruby SDK for Nightona: sandboxes, git, filesystem, LSP, process, and object storage.'
  spec.homepage = 'https://github.com/nightona-co/nightona'
  spec.required_ruby_version = '>= 3.2.0'

  spec.metadata['allowed_push_host'] = 'https://rubygems.org'

  spec.metadata['homepage_uri'] = spec.homepage
  spec.metadata['source_code_uri'] = 'https://github.com/nightona-co/nightona'
  spec.metadata['changelog_uri'] = 'https://github.com/nightona-co/nightona/releases'
  spec.metadata['rubygems_mfa_required'] = 'true'

  # Specify which files should be added to the gem when it is released.
  # The `git ls-files -z` loads the files in the RubyGem that have been added into git.
  gemspec = File.basename(__FILE__)
  spec.files = IO.popen(%w[git ls-files -z], chdir: __dir__, err: IO::NULL) do |ls|
    ls.readlines("\x0", chomp: true).reject do |f|
      (f == gemspec) ||
        f.start_with?(*%w[bin/ test/ spec/ features/ .git .github appveyor Gemfile])
    end
  end
  spec.bindir = 'exe'
  spec.executables = spec.files.grep(%r{\Aexe/}) { |f| File.basename(f) }
  spec.require_paths = ['lib']

  spec.add_dependency 'opentelemetry-exporter-otlp', '~> 0.29'
  spec.add_dependency 'opentelemetry-exporter-otlp-metrics', '~> 0.1'
  spec.add_dependency 'opentelemetry-metrics-sdk', '~> 0.2'
  spec.add_dependency 'opentelemetry-sdk', '~> 1.4'

  spec.add_dependency 'aws-sdk-s3', '~> 1.0'
  spec.add_dependency 'dotenv', '~> 2.0'
  spec.add_dependency 'nightona_api_client', Nightona::Sdk::VERSION
  spec.add_dependency 'nightona_toolbox_api_client', Nightona::Sdk::VERSION
  spec.add_dependency 'observer', '~> 0.1'
  spec.add_dependency 'toml', '~> 0.3'
  spec.add_dependency 'websocket-client-simple', '~> 0.6'
end
