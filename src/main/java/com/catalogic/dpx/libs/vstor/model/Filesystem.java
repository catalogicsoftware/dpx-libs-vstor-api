package com.catalogic.dpx.libs.vstor.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Filesystem(List<Directory> dirs, List<File> files, String path) {

  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public record Directory(String created, String modified, String link, String name) {}

  @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
  public record File(String created, String modified, String link, String name, long size) {}
}
