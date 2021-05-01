package net.cloud.imageprocessor.service;

import net.cloud.imageprocessor.config.JobUrlProvider;
import net.cloud.imageprocessor.model.Job;
import net.cloud.imageprocessor.model.JobSubmitRequest;
import net.cloud.imageprocessor.model.JsonWebToken;
import net.cloud.imageprocessor.util.HttpInvoker;
import net.cloud.imageprocessor.util.JwtHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ImageProcessorCoordinator {

    private final JwtHelper jwtHelper;
    private final HttpInvoker httpInvoker;
    private final JobUrlProvider jobUrlProvider;

    public ImageProcessorCoordinator(JwtHelper jwtHelper, JobUrlProvider jobUrlProvider, HttpInvoker httpInvoker) {
        this.jwtHelper = jwtHelper;
        this.httpInvoker = httpInvoker;
        this.jobUrlProvider = jobUrlProvider;
    }

    public ResponseEntity<String> submitJob(JobSubmitRequest jobSubmitRequest, String jwtString) {
        String url = jobUrlProvider.getSubmitUrl();
        JsonWebToken jwt = jwtHelper.parseClaims(jwtString);

        Job job = Job.builder()
                .clientId(jwt.getClientId())
                .tenantId(jwt.getTenantId())
                .payload(jobSubmitRequest.getContent())
                .payloadSize(jobSubmitRequest.getContent().length())
                .build();

        return httpInvoker.executePost(url, job, String.class);
    }

    public ResponseEntity<String> fetchJobStatus(Integer id) {
        String url = String.format(jobUrlProvider.getStatusUrl(), id);
        return httpInvoker.executeGet(url, String.class);
    }

    public ResponseEntity<byte[]> fetchJobResult(Integer id) {
        String url = String.format(jobUrlProvider.getResultUrl(), id);
        return httpInvoker.executeGet(url, byte[].class);
    }

}
