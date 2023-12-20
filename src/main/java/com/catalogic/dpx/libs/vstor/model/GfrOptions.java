package com.catalogic.dpx.libs.vstor.model;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.time.Instant;
import java.util.List;
import java.util.Map;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record GfrOptions(
    Map<String, List<Partition>> gfrImages,
    String gfrJobInstanceGuid,
    String gfrJobName,
    String gfrJobType,
    String gfrLogDestinationAddress,
    String gfrRecoveryPoint,
    Instant retentionTime) {}
