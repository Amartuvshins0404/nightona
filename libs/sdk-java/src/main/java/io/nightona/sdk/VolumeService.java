// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk;

import io.nightona.api.client.api.VolumesApi;
import io.nightona.api.client.model.CreateVolume;
import io.nightona.sdk.model.Volume;

import java.util.List;
import java.util.ArrayList;

/**
 * Service for managing Nightona Volumes.
 *
 * <p>Volumes provide persistent shared storage that can be mounted into Sandboxes.
 */
public class VolumeService {
    private final VolumesApi volumesApi;

    VolumeService(VolumesApi volumesApi) {
        this.volumesApi = volumesApi;
    }

    /**
     * Creates a new volume.
     *
     * @param name volume name
     * @return created {@link Volume}
     * @throws io.nightona.sdk.exception.NightonaException if creation fails
     */
    public Volume create(String name) {
        io.nightona.api.client.model.VolumeDto volumeDto = ExceptionMapper.callMain(
                () -> volumesApi.createVolume(new CreateVolume().name(name), null)
        );
        return toVolume(volumeDto);
    }

    /**
     * Lists all accessible volumes.
     *
     * @return list of available volumes
     * @throws io.nightona.sdk.exception.NightonaException if the API request fails
     */
    public List<Volume> list() {
        List<io.nightona.api.client.model.VolumeDto> volumes = ExceptionMapper.callMain(() -> volumesApi.listVolumes(null, null));
        List<Volume> result = new ArrayList<Volume>();
        if (volumes != null) {
            for (io.nightona.api.client.model.VolumeDto volume : volumes) {
                result.add(toVolume(volume));
            }
        }
        return result;
    }

    /**
     * Retrieves a volume by name.
     *
     * @param name volume name
     * @return matching {@link Volume}
     * @throws io.nightona.sdk.exception.NightonaException if no volume is found or request fails
     */
    public Volume getByName(String name) {
        io.nightona.api.client.model.VolumeDto volumeDto = ExceptionMapper.callMain(() -> volumesApi.getVolumeByName(name, null));
        return toVolume(volumeDto);
    }

    /**
     * Deletes a volume by ID.
     *
     * @param id volume identifier
     * @throws io.nightona.sdk.exception.NightonaException if deletion fails
     */
    public void delete(String id) {
        ExceptionMapper.runMain(() -> volumesApi.deleteVolume(id, null));
    }

    private Volume toVolume(io.nightona.api.client.model.VolumeDto source) {
        Volume volume = new Volume();
        if (source != null) {
            volume.setId(source.getId());
            volume.setName(source.getName());
            volume.setState(source.getState() == null ? null : source.getState().getValue());
        }
        return volume;
    }
}
