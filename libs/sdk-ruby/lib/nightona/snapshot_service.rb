# Copyright Nightona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

require 'uri'

module Nightona
  class SnapshotService
    include Instrumentation

    SNAPSHOTS_FETCH_LIMIT = 200

    # @param snapshots_api [NightonaApiClient::SnapshotsApi] The snapshots API client
    # @param object_storage_api [NightonaApiClient::ObjectStorageApi] The object storage API client
    # @param default_region_id [String, nil] Default region ID for snapshot creation
    # @param otel_state [Nightona::OtelState, nil]
    def initialize(snapshots_api:, object_storage_api:, default_region_id: nil, otel_state: nil)
      @snapshots_api = snapshots_api
      @object_storage_api = object_storage_api
      @default_region_id = default_region_id
      @otel_state = otel_state
    end

    # List all Snapshots.
    #
    # @param page [Integer, Nil]
    # @param limit [Integer, Nil]
    # @return [Nightona::PaginatedResource] Paginated list of all Snapshots
    # @raise [Nightona::Sdk::Error]
    #
    # @example
    #   nightona = Nightona::Nightona.new
    #   response = nightona.snapshot.list(page: 1, limit: 10)
    #   snapshots.items.each { |snapshot| puts "#{snapshot.name} (#{snapshot.image_name})" }
    def list(page: nil, limit: nil)
      raise Sdk::Error, 'page must be positive integer' if page && page < 1

      raise Sdk::Error, 'limit must be positive integer' if limit && limit < 1

      response = snapshots_api.get_all_snapshots(page:, limit:)
      PaginatedResource.new(
        total: response.total,
        page: response.page,
        total_pages: response.total_pages,
        items: response.items.map { |snapshot_dto| Snapshot.from_dto(snapshot_dto) }
      )
    end

    # Delete a Snapshot.
    #
    # @param snapshot [Nightona::Snapshot] Snapshot to delete
    # @return [void]
    #
    # @example
    #   nightona = Nightona::Nightona.new
    #   snapshot = nightona.snapshot.get("demo")
    #   nightona.snapshot.delete(snapshot)
    #   puts "Snapshot deleted"
    def delete(snapshot) = snapshots_api.remove_snapshot(snapshot.id)

    # Get a Snapshot by name.
    #
    # @param name [String] Name of the Snapshot to get
    # @return [Nightona::Snapshot] The Snapshot object
    #
    # @example
    #   nightona = Nightona::Nightona.new
    #   snapshot = nightona.snapshot.get("demo")
    #   puts "#{snapshot.name} (#{snapshot.image_name})"
    def get(name) = Snapshot.from_dto(snapshots_api.get_snapshot(name))

    # Creates and registers a new snapshot from the given Image definition.
    #
    # @param params [Nightona::CreateSnapshotParams] Parameters for snapshot creation
    # @param on_logs [Proc, Nil] Callback proc handling snapshot creation logs
    # @return [Nightona::Snapshot] The created snapshot
    #
    # @example
    #   image = Image.debianSlim('3.12').pipInstall('numpy')
    #   params = CreateSnapshotParams.new(name: 'my-snapshot', image: image)
    #   snapshot = nightona.snapshot.create(params) do |chunk|
    #     print chunk
    #   end
    def create(params, on_logs: nil) # rubocop:disable Metrics/AbcSize, Metrics/MethodLength
      create_snapshot_req = NightonaApiClient::CreateSnapshot.new(name: params.name)

      if params.image.is_a?(String)
        create_snapshot_req.image_name = params.image
        create_snapshot_req.entrypoint = params.entrypoint
      else
        create_snapshot_req.build_info = NightonaApiClient::CreateBuildInfo.new(
          context_hashes: self.class.process_image_context(object_storage_api, params.image),
          dockerfile_content: if params.entrypoint
                                params.image.entrypoint(params.entrypoint).dockerfile
                              else
                                params.image.dockerfile
                              end
        )
      end

      if params.resources
        create_snapshot_req.cpu = params.resources.cpu
        create_snapshot_req.gpu = params.resources.gpu
        if params.resources.gpu_type
          create_snapshot_req.gpu_type =
            params.resources.gpu_type.is_a?(Array) ? params.resources.gpu_type : [params.resources.gpu_type]
        end
        create_snapshot_req.memory = params.resources.memory
        create_snapshot_req.disk = params.resources.disk
      end

      create_snapshot_req.region_id = params.region_id || @default_region_id
      create_snapshot_req.sandbox_class = params.sandbox_class

      snapshot = snapshots_api.create_snapshot(create_snapshot_req)

      # Always wait for snapshot to be ready, regardless of on_logs
      snapshot = wait_for_snapshot(snapshot, on_logs:)

      if [NightonaApiClient::SnapshotState::ERROR, NightonaApiClient::SnapshotState::BUILD_FAILED].include?(snapshot.state)
        raise Sdk::Error, "Failed to create snapshot #{snapshot.name}, reason: #{snapshot.error_reason}"
      end

      Snapshot.from_dto(snapshot)
    end

    # Activate a snapshot
    #
    # @param snapshot [Nightona::Snapshot] The snapshot instance
    # @return [Nightona::Snapshot]
    def activate(snapshot) = Snapshot.from_dto(snapshots_api.activate_snapshot(snapshot.id))

    instrument :list, :delete, :get, :create, :activate, component: 'SnapshotService'

    # Processes the image context by uploading it to object storage
    #
    # @param image [Nightona::Image] The Image instance
    # @return [Array<String>] List of context hashes stored in object storage
    def self.process_image_context(object_storage_api, image) # rubocop:disable Metrics/AbcSize, Metrics/MethodLength
      return [] unless image.context_list && !image.context_list.empty?

      push_access_creds = object_storage_api.get_push_access

      object_storage = ObjectStorage.new(
        endpoint_url: push_access_creds.storage_url,
        aws_access_key_id: push_access_creds.access_key,
        aws_secret_access_key: push_access_creds.secret,
        aws_session_token: push_access_creds.session_token,
        bucket_name: push_access_creds.bucket
      )

      image.context_list.map do |context|
        object_storage.upload(
          context.source_path,
          push_access_creds.organization_id,
          context.archive_path
        )
      end
    end

    private

    # @return [NightonaApiClient::SnapshotsApi] The snapshots API client
    attr_reader :snapshots_api

    # @return [NightonaApiClient::ObjectStorageApi, nil] The object storage API client
    attr_reader :object_storage_api

    # @return [String, nil] Default region ID for snapshot creation
    attr_reader :default_region_id

    # @return [Nightona::OtelState, nil]
    attr_reader :otel_state

    # Wait for snapshot to reach a terminal state (ACTIVE, ERROR, or BUILD_FAILED)
    # Optionally streams logs if on_logs callback is provided
    #
    # @param snapshot [NightonaApiClient::SnapshotDto]
    # @param on_logs [Proc, nil]
    # @return [NightonaApiClient::SnapshotDto]
    def wait_for_snapshot(snapshot, on_logs:) # rubocop:disable Metrics/AbcSize, Metrics/MethodLength
      terminal_states = [
        NightonaApiClient::SnapshotState::ACTIVE,
        NightonaApiClient::SnapshotState::ERROR,
        NightonaApiClient::SnapshotState::BUILD_FAILED
      ]

      thread = nil
      previous_state = snapshot.state

      # Log initial state if callback provided
      on_logs&.call("Creating snapshot #{snapshot.name} (#{snapshot.state})")

      until terminal_states.include?(snapshot.state)
        Sdk.logger.debug("Waiting for snapshot to be created: #{snapshot.state}")

        # Start log streaming thread if callback provided and snapshot is building
        if on_logs && thread.nil? && snapshot.state != NightonaApiClient::SnapshotState::PENDING
          thread = start_log_streaming(snapshot, on_logs:)
        end

        # Log state changes if callback provided
        if on_logs && previous_state != snapshot.state
          if snapshot.state != NightonaApiClient::SnapshotState::PENDING && thread.nil?
            thread = start_log_streaming(snapshot, on_logs:)
          end
          on_logs.call("Creating snapshot #{snapshot.name} (#{snapshot.state})")
        end

        sleep(1)
        previous_state = snapshot.state
        snapshot = snapshots_api.get_snapshot(snapshot.id)
      end

      thread&.join

      if on_logs && snapshot.state == NightonaApiClient::SnapshotState::ACTIVE
        on_logs.call("Created snapshot #{snapshot.name} (#{snapshot.state})")
      end

      snapshot
    end

    # @param snapshot [NightonaApiClient::SnapshotDto]
    # @param on_logs [Proc]
    # @return [Thread]
    def start_log_streaming(snapshot, on_logs:)
      # Get build logs URL from API
      build_logs_response = snapshots_api.get_snapshot_build_logs_url(snapshot.id)
      uri = URI.parse("#{build_logs_response.url}?follow=true")

      headers = {}
      snapshots_api.api_client.update_params_for_auth!(headers, nil, ['bearer'])
      Util.stream_async(uri:, headers:, on_chunk: on_logs)
    end
  end
end
