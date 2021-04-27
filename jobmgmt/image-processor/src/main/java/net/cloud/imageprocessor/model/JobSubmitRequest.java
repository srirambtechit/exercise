package net.cloud.imageprocessor.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

@Data
public class JobSubmitRequest {
    @JsonAlias("MD5")
    private String md5Checksum;
    private String encoding;
    private String content;
}
