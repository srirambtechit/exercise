package net.cloud.imageprocessor.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Job {
    private final Integer id;
    private final String tenantId;
    private final String clientId;
    private final String payload; // base64 encoded image data
    private final Integer payloadSize;
}
