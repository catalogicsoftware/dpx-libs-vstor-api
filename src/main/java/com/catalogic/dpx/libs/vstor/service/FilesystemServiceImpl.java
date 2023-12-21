package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.model.Filesystem;
import com.catalogic.dpx.libs.vstor.model.MountedFilesystems;
import com.catalogic.dpx.libs.vstor.model.VstorConnection;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.core5.http.ContentType;

public class FilesystemServiceImpl extends VstorClient implements FilesystemService {
  private static final String MOUNTED_FILESYSTEMS_ENDPOINT = "/volume?type=mountedfilesystem";
  private static final String FILESYSTEM_ENDPOINT = "/volume/%s/filesystem%s?format=json";

  public FilesystemServiceImpl(VstorConnection vstorConnection) {
    super(vstorConnection);
  }

  @Override
  public MountedFilesystems getMountedFilesystems() {
    return get(getApiUrl() + MOUNTED_FILESYSTEMS_ENDPOINT, MountedFilesystems.class);
  }

  @Override
  public void uploadFile(int volumeId, String destinationPath, String fileName, byte[] fileBytes) {
    var httpEntity =
        MultipartEntityBuilder.create()
            .addBinaryBody(fileName, fileBytes, ContentType.TEXT_PLAIN, fileName)
            .build();

    putMultipart(
        getApiUrl() + FILESYSTEM_ENDPOINT.formatted(volumeId, destinationPath), httpEntity);
  }

  @Override
  public Filesystem getDirectoryContent(int volumeId, String directoryPath) {
    return get(
        getApiUrl() + FILESYSTEM_ENDPOINT.formatted(volumeId, directoryPath), Filesystem.class);
  }

  @Override
  public byte[] downloadFile(int volumeId, String filePath) {
    return getAndDownload(getApiUrl() + FILESYSTEM_ENDPOINT.formatted(volumeId, filePath));
  }
}
