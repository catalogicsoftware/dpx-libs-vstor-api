package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.model.SnapshotsByName;

public interface SnapshotService {
  SnapshotsByName getSnapshotsByName(String snapshotName);
}
