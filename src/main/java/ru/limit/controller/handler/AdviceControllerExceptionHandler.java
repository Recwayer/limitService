package ru.limit.controller.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.limit.dto.LimitResponse;
import ru.limit.exception.InsufficientLimitException;
import ru.limit.exception.LimitOperationException;
import ru.limit.exception.ValidationException;


@RestControllerAdvice
public class AdviceControllerExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(AdviceControllerExceptionHandler.class);

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<LimitResponse> handleValidationException(ValidationException ex) {
        log.warn("Validation error: {}", ex.getMessage());
        LimitResponse response = new LimitResponse(
                null, null, false, "Validation error: " + ex.getMessage()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InsufficientLimitException.class)
    public ResponseEntity<LimitResponse> handleInsufficientLimitException(InsufficientLimitException ex) {
        log.warn("Insufficient limit: {}", ex.getMessage());
        LimitResponse response = new LimitResponse(
                null, null, false, "Insufficient limit: " + ex.getMessage()
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(LimitOperationException.class)
    public ResponseEntity<LimitResponse> handleLimitOperationException(LimitOperationException ex) {
        log.error("Limit operation failed: {}", ex.getMessage(), ex);
        LimitResponse response = new LimitResponse(
                null, null, false, "Limit operation failed: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<LimitResponse> handleGenericException(Exception ex) {
        log.error("Unexpected error occurred: {}", ex.getMessage(), ex);
        LimitResponse response = new LimitResponse(
                null, null, false, "Internal server error: " + ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
