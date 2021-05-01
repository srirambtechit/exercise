package net.cloud.imageprocessor.exception;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Log4j2
@ControllerAdvice
public class ImageProcessorExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> authErrorHandler(UnauthorizedException exception) {
        log.error("Unauthorized error: {}", exception.getMessage());

        // additional error handling

        return ResponseEntity.status(exception.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(exception.prepareMessage());
    }

    @ExceptionHandler(ChecksumNotMatchException.class)
    public ResponseEntity<String> checksumErrorHandler(ChecksumNotMatchException exception) {
        log.error("Checksum error: {}", exception.getMessage());

        // additional error handling

        return ResponseEntity.status(exception.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(exception.prepareMessage());
    }

    @ExceptionHandler(ImageProcessorException.class)
    public ResponseEntity<String> imageProcessorErrorHandler(ImageProcessorException exception) {
        log.error("Image processor error: {}", exception.getMessage());

        // additional error handling

        return ResponseEntity.status(exception.getHttpStatus())
                .contentType(MediaType.APPLICATION_JSON)
                .body(exception.prepareMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> allExceptionHandler(Exception exception) {
        log.error("Generic error: {}", exception.getMessage());

        // additional error handling

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.APPLICATION_JSON)
                .body(AbstractApplicationException.defaultMessage(exception));
    }
}
