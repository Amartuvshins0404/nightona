import os

from nightona import CreateSandboxFromSnapshotParams, Nightona, VolumeMount


def main():
    nightona = Nightona()

    # Create a new volume or get an existing one
    volume = nightona.volume.get("my-volume", create=True)

    # Mount the volume to the sandbox
    mount_dir_1 = "/home/nightona/volume"

    params = CreateSandboxFromSnapshotParams(
        language="python",
        volumes=[VolumeMount(volume_id=volume.id, mount_path=mount_dir_1)],
    )
    sandbox = nightona.create(params)

    # Create a new directory in the mount directory
    new_dir = os.path.join(mount_dir_1, "new-dir")
    sandbox.fs.create_folder(new_dir, "755")

    # Create a new file in the mount directory
    new_file = os.path.join(mount_dir_1, "new-file.txt")
    sandbox.fs.upload_file(b"Hello, World!", new_file)

    # Create a new sandbox with the same volume
    # and mount it to the different path
    mount_dir_2 = "/home/nightona/my-files"

    params = CreateSandboxFromSnapshotParams(
        language="python",
        volumes=[VolumeMount(volume_id=volume.id, mount_path=mount_dir_2)],
    )
    sandbox2 = nightona.create(params)

    # List files in the mount directory
    files = sandbox2.fs.list_files(mount_dir_2)
    print("Files:", files)

    # Get the file from the mount directory
    file = sandbox2.fs.download_file(os.path.join(mount_dir_2, "new-file.txt"))
    print("File:", file)

    # Mount a specific subpath within the volume
    # This is useful for isolating data or implementing multi-tenancy
    mount_dir_3 = "/home/nightona/subpath"

    params = CreateSandboxFromSnapshotParams(
        language="python",
        volumes=[VolumeMount(volume_id=volume.id, mount_path=mount_dir_3, subpath="users/alice")],
    )
    sandbox3 = nightona.create(params)

    # This sandbox will only see files within the 'users/alice' subpath
    # Create a file in the subpath
    subpath_file = os.path.join(mount_dir_3, "alice-file.txt")
    sandbox3.fs.upload_file(b"Hello from Alice's subpath!", subpath_file)

    # The file is stored at: volume-root/users/alice/alice-file.txt
    # but appears at: /home/nightona/subpath/alice-file.txt in the sandbox

    # Cleanup
    nightona.delete(sandbox)
    nightona.delete(sandbox2)
    nightona.delete(sandbox3)
    # nightona.volume.delete(volume)


if __name__ == "__main__":
    main()
