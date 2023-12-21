package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.model.Share;
import com.catalogic.dpx.libs.vstor.model.VstorConnection;

public class ShareServiceImpl extends VstorClient implements ShareService {
  private static final String SHARE_BY_ID = "/share/%s";

  public ShareServiceImpl(VstorConnection vstorConnection) {
    super(vstorConnection);
  }

  @Override
  public Share getShareById(int shareId) {
    return get(getApiUrl() + SHARE_BY_ID.formatted(shareId), Share.class);
  }
}
