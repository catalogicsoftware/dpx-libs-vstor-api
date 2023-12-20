package com.catalogic.dpx.libs.vstor.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.Instant;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record Volume(
    boolean autoUnlock,
    boolean encryption,
    String encryptionState,
    int id,
    boolean isClone,
    String name,
    String poolId,
    String sizeFree,
    String sizeTotal,
    String sizeUsed,
    String state,
    Instant timeCreated,
    Instant timeUpdated,
    String volumeType,
    int shareId) {}
