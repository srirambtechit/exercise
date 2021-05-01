package net.cloud.imageprocessor.model;

import lombok.Builder;
import lombok.Getter;

import java.util.Date;

@Getter
@Builder
public class JsonWebToken {
    private final String subject;
    private final Date issueAt;
    private final String audience;
    private final String name;
    private final String tenantId;
    private final String clientId;
    private final String appId;
    private final String email;
}
