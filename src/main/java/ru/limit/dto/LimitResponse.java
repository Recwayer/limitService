package ru.limit.dto;

import java.math.BigDecimal;

public record LimitResponse(Long userId, BigDecimal currentLimit, boolean success, String message) {
}
