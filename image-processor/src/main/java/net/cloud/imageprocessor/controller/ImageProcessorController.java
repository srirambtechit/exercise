package net.cloud.imageprocessor.controller;

import lombok.extern.log4j.Log4j2;
import net.cloud.imageprocessor.model.JobSubmitRequest;
import net.cloud.imageprocessor.service.ImageProcessorCoordinator;
import net.cloud.imageprocessor.util.JwtHelper;
import net.cloud.imageprocessor.util.RequestMessageVerifier;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static net.cloud.imageprocessor.util.Constants.X_JWT_TOKEN;

@Log4j2
@RestController
public class ImageProcessorController {
    private final RequestMessageVerifier requestMessageVerifier;
    private final ImageProcessorCoordinator imageProcessorCoordinator;

    public ImageProcessorController(RequestMessageVerifier requestMessageVerifier,
                                    ImageProcessorCoordinator imageProcessorCoordinator) {
        this.requestMessageVerifier = requestMessageVerifier;
        this.imageProcessorCoordinator = imageProcessorCoordinator;
    }

    @PostMapping(value = "/job")
    public ResponseEntity<String> createJob(@RequestHeader(X_JWT_TOKEN) String jwt,
                                            @RequestBody JobSubmitRequest jobRequest) {
        log.info("Job payload {}", jobRequest);
        requestMessageVerifier.validateChecksum(jobRequest.getMd5Checksum(), jobRequest.getContent());
        return imageProcessorCoordinator.submitJob(jobRequest, jwt);
    }

    @GetMapping("/job/{id}/status")
    public ResponseEntity<String> getJobStatus(@PathVariable Integer id) {
        log.info("Job status {}", id);
        return imageProcessorCoordinator.fetchJobStatus(id);
    }

    @GetMapping(value = "/job/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getJobResult(@PathVariable Integer id) {
        log.info("Job result {}", id);
        return imageProcessorCoordinator.fetchJobResult(id);
    }

    // for the purpose of PoC, having /login
    @GetMapping("/login")
    public ResponseEntity<String> authenticate() {
        return ResponseEntity.ok(JwtHelper.generate());
    }
}
