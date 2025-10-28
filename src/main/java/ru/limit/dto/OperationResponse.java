package ru.limit.dto;

import java.math.BigDecimal;

public record OperationResponse(String operationId, Long userId, BigDecimal amount, boolean success, String message) {
}
