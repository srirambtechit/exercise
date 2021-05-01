package net.cloud.imageprocessor.exception;

import org.springframework.http.HttpStatus;

import java.util.Objects;

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
        String message = getMessage();
        message = message.replaceAll("\"", "'");

        String resolution = getResolution();
        String issueAndResolution = getIssue() + ", " + resolution;
        issueAndResolution = issueAndResolution.replaceAll("\"", "'");

        String error;
        if (Objects.isNull(resolution))
            error = String.format("{\"code\": %d, \"error\": \"%s\"}",
                    httpStatus.value(),
                    message);
        else
            error = String.format("{\"code\": %d, \"error\": \"%s\", \"issueAndResolution\": \"%s\"}",
                    httpStatus.value(),
                    message,
                    issueAndResolution);

        return error;
    }

    public static String defaultMessage(Exception exception) {
        String message = exception.getMessage()
                .replaceAll("\"", "'");

        return String.format("{\"error\": \"%s\", \"message\": \"%s\"}",
                "Server Unhandled",
                message);
    }
}
