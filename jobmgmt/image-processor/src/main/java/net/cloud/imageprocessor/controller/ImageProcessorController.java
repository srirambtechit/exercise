package net.cloud.imageprocessor.controller;

import lombok.extern.log4j.Log4j2;
import net.cloud.imageprocessor.model.JobSubmitRequest;
import net.cloud.imageprocessor.service.ImageProcessorCoordinator;
import net.cloud.imageprocessor.util.JwtHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
public class ImageProcessorController {

    private final ImageProcessorCoordinator imageProcessorCoordinator;

    @Autowired
    public ImageProcessorController(ImageProcessorCoordinator imageProcessorCoordinator) {
        this.imageProcessorCoordinator = imageProcessorCoordinator;
    }

    @PostMapping(value = "/job")
    public ResponseEntity<String> createJob(@RequestHeader("X-Jwt-Token") String jwt,
                                            @RequestBody JobSubmitRequest jobRequest) {
        log.info("Job payload {}", jobRequest);
        imageProcessorCoordinator.validate(jwt, jobRequest);
        return imageProcessorCoordinator.submitJob(jobRequest, jwt);
    }

    @GetMapping("/job/{id}/status")
    public ResponseEntity<String> getJobStatus(@PathVariable Integer id, @RequestHeader("X-Jwt-Token") String jwt) {
        log.info("Job status {}", id);
        imageProcessorCoordinator.validate(jwt, null);
        return imageProcessorCoordinator.fetchJobStatus(id);
    }

    @GetMapping(value = "/job/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> getJobResult(@PathVariable Integer id, @RequestHeader("X-Jwt-Token") String jwt) {
        log.info("Job result {}", id);
        imageProcessorCoordinator.validate(jwt, null);
        return imageProcessorCoordinator.fetchJobResult(id);
    }

    // for the purpose of PoC, having /login
    @GetMapping("/login")
    public ResponseEntity<String> authenticate() {
        return ResponseEntity.ok(JwtHelper.generate());
    }
}
