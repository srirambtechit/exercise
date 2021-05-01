package net.cloud.imageprocessor.exception;

import org.springframework.http.HttpStatus;

public class ImageProcessorException extends AbstractApplicationException {

    public ImageProcessorException(HttpStatus httpStatus, String message) {
        super(httpStatus, message);
    }

    @Override
    String getResolution() {
        return null;
    }

    @Override
    String getIssue() {
        return getMessage();
    }

}
