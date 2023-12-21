package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.model.Volume;
import com.catalogic.dpx.libs.vstor.model.VolumeFromSnapshotRequest;
import com.catalogic.dpx.libs.vstor.model.VolumeList;

public interface VolumeService {
  Volume getVolumeById(int volumeId);

  VolumeList getVolumes();

  VolumeList getVolumesByType(String type);

  Volume getVolumeByName(String volumeName);

  Volume createVolumeFromSnapshot(VolumeFromSnapshotRequest request);
}
