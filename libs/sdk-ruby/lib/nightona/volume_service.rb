# Copyright Daytona Platforms Inc.
# SPDX-License-Identifier: Apache-2.0

# frozen_string_literal: true

module Nightona
  class VolumeService
    include Instrumentation

    # Service for managing Nightona Volumes. Can be used to list, get, create and delete Volumes.
    #
    # @param volumes_api [NightonaApiClient::VolumesApi]
    # @param otel_state [Nightona::OtelState, nil]
    def initialize(volumes_api, otel_state: nil)
      @volumes_api = volumes_api
      @otel_state = otel_state
    end

    # Create new Volume.
    #
    # @param name [String]
    # @return [Nightona::Volume]
    def create(name) = Volume.new(volumes_api.create_volume(NightonaApiClient::CreateVolume.new(name:)))

    # Delete a Volume.
    #
    # @param volume [Nightona::Volume]
    # @return [void]
    def delete(volume) = volumes_api.delete_volume(volume.id)

    # Get a Volume by name.
    #
    # @param name [String]
    # @param create [Boolean]
    # @return [Nightona::Volume]
    def get(name, create: false)
      Volume.new(volumes_api.get_volume_by_name(name))
    rescue NightonaApiClient::ApiError => e
      raise unless create && e.code == 404 && e.message.include?("Volume with name #{name} not found")

      create(name)
    end

    # List all Volumes.
    #
    # @return [Array<Nightona::Volume>]
    def list
      volumes_api.list_volumes.map { |volume| Volume.new(volume) }
    end

    instrument :create, :delete, :get, :list, component: 'VolumeService'

    private

    # @return [NightonaApiClient::VolumesApi]
    attr_reader :volumes_api

    # @return [Nightona::OtelState, nil]
    attr_reader :otel_state
  end
end
