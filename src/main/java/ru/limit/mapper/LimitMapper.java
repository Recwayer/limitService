package ru.limit.mapper;

import org.springframework.stereotype.Component;
import ru.limit.dto.LimitResponse;
import ru.limit.dto.OperationResponse;
import ru.limit.model.Limit;
import ru.limit.model.ReservedLimit;

import java.math.BigDecimal;

@Component
public class LimitMapper {

    public LimitResponse toLimitResponse(Limit limit) {
        return new LimitResponse(
                limit.getUserId(),
                limit.getCurrentLimit(),
                true,
                "Success"
        );
    }

    public LimitResponse toLimitResponseWithReserved(Limit limit, BigDecimal reservedAmount, BigDecimal availableLimit) {
        return new LimitResponse(
                limit.getUserId(),
                availableLimit,
                true,
                "Limit retrieved successfully. Reserved: " + reservedAmount
        );
    }

    public OperationResponse toOperationResponse(ReservedLimit reservedLimit, boolean success, String message) {
        return new OperationResponse(
                reservedLimit.getOperationId(),
                reservedLimit.getUserId(),
                reservedLimit.getAmount(),
                success,
                message
        );
    }


    public OperationResponse toDecreaseOperationResponse(Long userId, BigDecimal amount, BigDecimal newLimit) {
        return new OperationResponse(
                "direct-" + System.currentTimeMillis(),
                userId,
                amount,
                true,
                "Limit decreased successfully. New limit: " + newLimit
        );
    }

    public OperationResponse toIncreaseOperationResponse(Long userId, BigDecimal amount, BigDecimal newLimit) {
        return new OperationResponse(
                "increase-" + System.currentTimeMillis(),
                userId,
                amount,
                true,
                "Limit increased successfully. New limit: " + newLimit
        );
    }
}