package ru.limit.exception;

import java.math.BigDecimal;

public class InsufficientLimitException extends RuntimeException {
    public InsufficientLimitException(Long userId, BigDecimal currentLimit, BigDecimal requestedAmount) {
        super(String.format("Insufficient limit for user %d. Current: %s, Requested: %s",
                userId, currentLimit, requestedAmount));
    }
}
