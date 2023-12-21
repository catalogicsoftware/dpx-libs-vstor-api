package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.model.Filesystem;
import com.catalogic.dpx.libs.vstor.model.MountedFilesystems;

public interface FilesystemService {
  MountedFilesystems getMountedFilesystems();

  void uploadFile(int volumeId, String destinationPath, String fileName, byte[] fileBytes);

  Filesystem getDirectoryContent(int volumeId, String directoryPath);

  byte[] downloadFile(int volumeId, String filePath);
}
