package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.model.MountedFilesystems;
import com.catalogic.dpx.libs.vstor.model.VstorConnection;

public class FilesystemServiceImpl extends VstorClient implements FilesystemService {
  private static final String MOUNTED_FILESYSTEMS_ENDPOINT = "/volume?type=mountedfilesystem";

  public FilesystemServiceImpl(VstorConnection vstorConnection) {
    super(vstorConnection);
  }

  @Override
  public MountedFilesystems getMountedFilesystems() {
    return get(getApiUrl() + MOUNTED_FILESYSTEMS_ENDPOINT, MountedFilesystems.class);
  }
}
