package com.catalogic.dpx.libs.vstor.service;

import com.catalogic.dpx.libs.vstor.model.GfrOptions;
import com.catalogic.dpx.libs.vstor.model.VstorConnection;

public class GfrServiceImpl extends VstorClient implements GfrService {
  private static final String GFR_OPTIONS_ENDPOINT_FORMAT = "/volume/%s/gfr_options";

  public GfrServiceImpl(VstorConnection vstorConnection) {
    super(vstorConnection);
  }

  @Override
  public GfrOptions getGfrOptions(int volumeId) {
    return get(getApiUrl() + GFR_OPTIONS_ENDPOINT_FORMAT.formatted(volumeId), GfrOptions.class);
  }
}
