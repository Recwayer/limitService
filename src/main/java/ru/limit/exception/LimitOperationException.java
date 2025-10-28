package ru.limit.exception;

public class LimitOperationException extends RuntimeException {
    public LimitOperationException(String message) {
        super(message);
    }

    public LimitOperationException(String message, Throwable cause) {
        super(message, cause);
    }
}
