package ru.limit.dto;

import java.math.BigDecimal;

public record ReserveRequest(Long userId, String operationId, BigDecimal amount) {
}
