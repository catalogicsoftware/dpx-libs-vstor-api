package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.model.Share;
import com.catalogic.dpx.libs.vstor.model.ShareRequest;

public interface ShareService {
  Share getShareById(int shareId);

  Share createShare(ShareRequest request);
}
