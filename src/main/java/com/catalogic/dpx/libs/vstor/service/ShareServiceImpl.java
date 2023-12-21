package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.model.Share;
import com.catalogic.dpx.libs.vstor.model.ShareRequest;
import com.catalogic.dpx.libs.vstor.model.VstorConnection;
import java.net.http.HttpRequest.BodyPublishers;
import java.nio.charset.StandardCharsets;

public class ShareServiceImpl extends VstorClient implements ShareService {
  private static final String SHARE_BY_ID = "/share/%s";
  private static final String SHARE = "/share";

  public ShareServiceImpl(VstorConnection vstorConnection) {
    super(vstorConnection);
  }

  @Override
  public Share getShareById(int shareId) {
    return get(getApiUrl() + SHARE_BY_ID.formatted(shareId), Share.class);
  }

  @Override
  public Share createShare(ShareRequest request) {
    return post(
        getApiUrl() + SHARE,
        BodyPublishers.ofString(serializeToJson(request), StandardCharsets.UTF_8),
        Share.class);
  }
}
