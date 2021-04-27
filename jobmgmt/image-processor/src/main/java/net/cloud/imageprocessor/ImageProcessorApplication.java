package net.cloud.imageprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("net.cloud.imageprocessor.config")
public class ImageProcessorApplication {

    public static void main(String[] args) {
        SpringApplication.run(ImageProcessorApplication.class, args);
    }

}
