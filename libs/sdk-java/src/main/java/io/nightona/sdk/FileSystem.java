// Copyright Nightona Platforms Inc.
// SPDX-License-Identifier: Apache-2.0

package io.nightona.sdk;

import io.nightona.sdk.model.FileInfo;
import io.nightona.toolbox.client.api.FileSystemApi;
import io.nightona.toolbox.client.model.ReplaceRequest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * File system operations facade for a specific Sandbox.
 *
 * <p>Provides methods for directory management, file upload/download, metadata inspection, and
 * search/replace operations.
 */
public class FileSystem {
    private final FileSystemApi fileSystemApi;

    FileSystem(FileSystemApi fileSystemApi) {
        this.fileSystemApi = fileSystemApi;
    }

    /**
     * Creates a directory in the Sandbox.
     *
     * @param path directory path
     * @param mode POSIX mode (for example {@code 755}); defaults to {@code 755} when {@code null}
     * @throws io.nightona.sdk.exception.NightonaException if creation fails
     */
    public void createFolder(String path, String mode) {
        ExceptionMapper.runToolbox(() -> fileSystemApi.createFolder(path, mode == null ? "755" : mode));
    }

    /**
     * Deletes a file.
     *
     * @param path file path to delete
     * @throws io.nightona.sdk.exception.NightonaException if deletion fails
     */
    public void deleteFile(String path) {
        ExceptionMapper.runToolbox(() -> fileSystemApi.deleteFile(path, null));
    }

    /**
     * Downloads a file into memory.
     *
     * @param remotePath source file path in the Sandbox
     * @return file bytes; empty array when no file payload is returned
     * @throws io.nightona.sdk.exception.NightonaException if download or local read fails
     */
    public byte[] downloadFile(String remotePath) {
        File file = ExceptionMapper.callToolbox(() -> fileSystemApi.downloadFile(remotePath));
        if (file == null) {
            return new byte[0];
        }
        try {
            byte[] bytes = Files.readAllBytes(file.toPath());
            Files.deleteIfExists(file.toPath());
            return bytes;
        } catch (IOException e) {
            throw new io.nightona.sdk.exception.NightonaException("Failed to read downloaded file", e);
        }
    }

    /**
     * Downloads a single file from the Sandbox as a stream without buffering the entire file
     * into memory. The returned {@link InputStream} can be piped directly to an HTTP response,
     * written to a file, or processed on the fly.
     *
     * <p>The caller is responsible for closing the returned stream.
     *
     * @param remotePath source file path in the Sandbox
     * @return an {@link InputStream} streaming the file content
     * @throws io.nightona.sdk.exception.NightonaException if the file does not exist or access is denied
     */
    public InputStream downloadFileStream(String remotePath) throws io.nightona.sdk.exception.NightonaException {
        return downloadFileStream(remotePath, new DownloadStreamOptions());
    }

    /**
     * Downloads a single file from the Sandbox as a stream without buffering the entire file
     * into memory, with a custom timeout.
     *
     * <p>The caller is responsible for closing the returned stream.
     *
     * @param remotePath source file path in the Sandbox
     * @param timeoutSeconds timeout in seconds; 0 means no timeout
     * @return an {@link InputStream} streaming the file content
     * @throws io.nightona.sdk.exception.NightonaException if the file does not exist or access is denied
     */
    public InputStream downloadFileStream(String remotePath, int timeoutSeconds) throws io.nightona.sdk.exception.NightonaException {
        return downloadFileStream(remotePath, new DownloadStreamOptions().setTimeout(timeoutSeconds));
    }

    /**
     * Downloads a single file from the Sandbox as a stream with configurable options.
     *
     * @param remotePath source file path in the Sandbox
     * @param options download options including timeout and progress callback
     * @return an InputStream streaming the file content
     * @throws io.nightona.sdk.exception.NightonaException if download fails
     */
    public InputStream downloadFileStream(String remotePath, DownloadStreamOptions options) throws io.nightona.sdk.exception.NightonaException {
        return FileTransfer.streamDownload(fileSystemApi, remotePath, options != null ? options : new DownloadStreamOptions());
    }

    /**
     * Uploads in-memory file content to a Sandbox path.
     *
     * @param content file bytes; {@code null} uploads an empty file
     * @param remotePath destination file path in the Sandbox
     * @throws io.nightona.sdk.exception.NightonaException if upload fails
     */
    public void uploadFile(byte[] content, String remotePath) {
        try {
            File tempFile = File.createTempFile("nightona-upload-", ".tmp");
            Files.write(tempFile.toPath(), content == null ? new byte[0] : content);
            ExceptionMapper.callToolbox(() -> fileSystemApi.uploadFile(remotePath, tempFile));
            Files.deleteIfExists(tempFile.toPath());
        } catch (IOException e) {
            throw new io.nightona.sdk.exception.NightonaException("Failed to upload file", e);
        }
    }

    /**
     * Streams an upload to a Sandbox path without buffering the source. The bytes are
     * piped through a progress-counting wrapper directly into a streaming multipart
     * request, so heap usage stays flat regardless of source size.
     *
     * @param source the data source; the caller retains ownership and is responsible for closing it
     * @param remotePath destination file path in the Sandbox
     * @param options upload options including timeout, cancellation, and progress callback
     * @throws io.nightona.sdk.exception.NightonaException if upload fails
     */
    public void uploadFileStream(InputStream source, String remotePath, UploadStreamOptions options) {
        FileTransfer.streamUpload(fileSystemApi, source, remotePath, options != null ? options : new UploadStreamOptions());
    }

    /** Convenience overload using default options. */
    public void uploadFileStream(InputStream source, String remotePath) {
        uploadFileStream(source, remotePath, new UploadStreamOptions());
    }

    /**
     * Lists files and directories under a path.
     *
     * @param path directory path
     * @return file metadata entries
     * @throws io.nightona.sdk.exception.NightonaException if listing fails
     */
    public List<FileInfo> listFiles(String path) {
        List<io.nightona.toolbox.client.model.FileInfo> files = ExceptionMapper.callToolbox(() -> fileSystemApi.listFiles(path));
        List<FileInfo> result = new ArrayList<FileInfo>();
        if (files != null) {
            for (io.nightona.toolbox.client.model.FileInfo file : files) {
                result.add(new FileInfo(file));
            }
        }
        return result;
    }

    /**
     * Returns metadata for a single file or directory.
     *
     * @param path file or directory path
     * @return metadata record
     * @throws io.nightona.sdk.exception.NightonaException if lookup fails
     */
    public FileInfo getFileDetails(String path) {
        io.nightona.toolbox.client.model.FileInfo fileInfo = ExceptionMapper.callToolbox(() -> fileSystemApi.getFileInfo(path));
        return new FileInfo(fileInfo);
    }

    /**
     * Searches files by content pattern.
     *
     * @param path root directory to search
     * @param pattern text pattern to find
     * @return list of matches containing file, line, and content
     * @throws io.nightona.sdk.exception.NightonaException if the search request fails
     */
    public List<Map<String, Object>> findFiles(String path, String pattern) {
        List<io.nightona.toolbox.client.model.Match> matches = ExceptionMapper.callToolbox(() -> fileSystemApi.findInFiles(path, pattern));
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (matches != null) {
            for (io.nightona.toolbox.client.model.Match match : matches) {
                Map<String, Object> item = new HashMap<String, Object>();
                item.put("content", match.getContent());
                item.put("file", match.getFile());
                item.put("line", match.getLine());
                result.add(item);
            }
        }
        return result;
    }

    /**
     * Searches files by file-name pattern.
     *
     * @param path root directory to search
     * @param pattern file-name pattern
     * @return result map containing {@code files}
     * @throws io.nightona.sdk.exception.NightonaException if the search request fails
     */
    public Map<String, Object> searchFiles(String path, String pattern) {
        io.nightona.toolbox.client.model.SearchFilesResponse response = ExceptionMapper.callToolbox(() -> fileSystemApi.searchFiles(path, pattern));
        Map<String, Object> result = new HashMap<String, Object>();
        result.put("files", response == null ? new ArrayList<String>() : response.getFiles());
        return result;
    }

    /**
     * Performs in-place replacement in multiple files.
     *
     * @param files files to process
     * @param pattern pattern to replace
     * @param newValue replacement text
     * @throws io.nightona.sdk.exception.NightonaException if replacement fails
     */
    public void replaceInFiles(List<String> files, String pattern, String newValue) {
        ReplaceRequest request = new ReplaceRequest().files(files).pattern(pattern).newValue(newValue);
        ExceptionMapper.callToolbox(() -> fileSystemApi.replaceInFiles(request));
    }

    /**
     * Moves or renames a file or directory.
     *
     * @param source source path
     * @param destination destination path
     * @throws io.nightona.sdk.exception.NightonaException if move fails
     */
    public void moveFiles(String source, String destination) {
        ExceptionMapper.runToolbox(() -> fileSystemApi.moveFile(source, destination));
    }

}
