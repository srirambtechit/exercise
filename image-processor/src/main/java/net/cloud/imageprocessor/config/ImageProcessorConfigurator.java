package net.cloud.imageprocessor.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class ImageProcessorConfigurator {
    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
