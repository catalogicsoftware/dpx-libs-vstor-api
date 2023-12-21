package com.catalogic.dpx.libs.vstor.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record ShareRequest(int volId, ShareType shareType, ShareOptions shareOptions) {

  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public record ShareOptions(boolean readOnly, List<String> allowedHosts) {}

  public enum ShareType {
    nfs,
    smb
  }
}
