package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.model.Volume;
import com.catalogic.dpx.libs.vstor.model.VolumeList;
import com.catalogic.dpx.libs.vstor.model.VstorConnection;

public class VolumeServiceImpl extends VstorClient implements VolumeService {
  private static final String VOLUME_BY_ID = "/volume?id=%s";
  private static final String VOLUMES = "/volume";
  private static final String VOLUMES_BY_TYPE = "/volume?type=%s";
  private static final String VOLUME_BY_NAME = "/volume?name=%s";

  public VolumeServiceImpl(VstorConnection vstorConnection) {
    super(vstorConnection);
  }

  @Override
  public Volume getVolumeById(int volumeId) {
    return get(getApiUrl() + VOLUME_BY_ID.formatted(volumeId), Volume.class);
  }

  @Override
  public VolumeList getVolumes() {
    return get(getApiUrl() + VOLUMES, VolumeList.class);
  }

  @Override
  public VolumeList getVolumesByType(String type) {
    return get(getApiUrl() + VOLUMES_BY_TYPE.formatted(type), VolumeList.class);
  }

  @Override
  public Volume getVolumeByName(String volumeName) {
    return get(getApiUrl() + VOLUME_BY_NAME.formatted(volumeName), Volume.class);
  }
}
