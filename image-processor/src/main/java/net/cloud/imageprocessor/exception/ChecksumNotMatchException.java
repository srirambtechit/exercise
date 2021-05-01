package net.cloud.imageprocessor.exception;

import org.springframework.http.HttpStatus;

public class ChecksumNotMatchException extends AbstractApplicationException {

    public ChecksumNotMatchException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }

    @Override
    String getResolution() {
        return "check your checksum data";
    }

    @Override
    String getIssue() {
        return "Bad Input";
    }
}
