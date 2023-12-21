package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.model.Filesystem;
import com.catalogic.dpx.libs.vstor.model.MountedFilesystems;
import com.catalogic.dpx.libs.vstor.model.VstorConnection;

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
  public void uploadFile(int volumeId, String destinationPath) {}

  @Override
  public Filesystem getDirectoryContent(int volumeId, String directoryPath) {
    return get(
        getApiUrl() + FILESYSTEM_ENDPOINT.formatted(volumeId, directoryPath), Filesystem.class);
  }

  @Override
  public byte[] downloadFile(int volumeId, String filePath) {
    return null;
  }
}
