package net.cloud.imageprocessor.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "jobs")
public class JobUrlProvider {
    private String submitUrl;
    private String statusUrl;
    private String resultUrl;
}
