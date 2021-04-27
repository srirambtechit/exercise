package net.cloud.imageprocessor.util;

import net.cloud.imageprocessor.model.Job;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HttpInvoker {
    private final RestTemplate restTemplate;

    public HttpInvoker(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public <T> ResponseEntity<T> executeGet(String url, Class<T> responseType) {
        return restTemplate.getForEntity(url, responseType);
    }

    public <T> ResponseEntity<T> executePost(String url, Job job, Class<T> responseType) {
        return restTemplate.postForEntity(url, job, responseType);
    }
}
