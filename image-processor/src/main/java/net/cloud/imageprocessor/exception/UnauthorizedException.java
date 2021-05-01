package net.cloud.imageprocessor.exception;

import org.springframework.http.HttpStatus;

public class UnauthorizedException extends AbstractApplicationException {

    public UnauthorizedException(String error) {
        super(HttpStatus.UNAUTHORIZED, error);
    }

    @Override
    String getResolution() {
        return "check your jwt token";
    }

    @Override
    String getIssue() {
        return "Unauthorized Access";
    }
}
