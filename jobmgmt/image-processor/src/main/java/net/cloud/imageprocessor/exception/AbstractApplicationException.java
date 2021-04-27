package net.cloud.imageprocessor.exception;

import org.springframework.http.HttpStatus;

public abstract class AbstractApplicationException extends RuntimeException {

    protected HttpStatus httpStatus;

    public AbstractApplicationException(HttpStatus httpStatus, String message) {
        super(message);
        this.httpStatus = httpStatus;
    }

    abstract String getResolution();

    abstract String getIssue();

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String prepareMessage() {
        return "{" +
                "\"error\": " + httpStatus.value() + ", " +
                "\"message\": " + getIssue() + ", " + getResolution() +
                "}";
    }
}
