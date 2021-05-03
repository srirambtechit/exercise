package net.cloud.imageprocessor.service;

import net.cloud.imageprocessor.model.JobSubmitRequest;
import org.springframework.http.ResponseEntity;

public interface ImageProcessorTask {
    ResponseEntity<String> submitJob(JobSubmitRequest jobSubmitRequest, String jwtString);

    ResponseEntity<String> fetchJobStatus(Integer id);

    ResponseEntity<byte[]> fetchJobResult(Integer id);
}
