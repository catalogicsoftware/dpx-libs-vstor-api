package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.model.Snapshot;
import com.catalogic.dpx.libs.vstor.model.SnapshotsByName;
import com.catalogic.dpx.libs.vstor.model.VstorConnection;
import java.util.Collections;
import java.util.List;

public class SnapshotServiceImpl extends VstorClient implements SnapshotService {
  private static final String SNAPSHOTS_BY_NAME = "/snapshot?name=%s";

  public SnapshotServiceImpl(VstorConnection vstorConnection) {
    super(vstorConnection);
  }

  @Override
  public SnapshotsByName getSnapshotsByName(String snapshotName) {
    var requestUrl = getApiUrl() + SNAPSHOTS_BY_NAME.formatted(snapshotName);
    var snapshotByName = get(requestUrl, SnapshotsByName.class);
    return (snapshotByName == null || snapshotByName.snapshots() == null)
        ? getSingleSnapshotByName(requestUrl)
        : snapshotByName;
  }

  private SnapshotsByName getSingleSnapshotByName(String requestUrl) {
    var singleSnapshot = get(requestUrl, Snapshot.class);
    return singleSnapshot != null
        ? new SnapshotsByName(1, List.of(singleSnapshot))
        : new SnapshotsByName(0, Collections.emptyList());
  }
}
