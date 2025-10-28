package ru.limit.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.limit.configuration.properties.LimitProperties;
import ru.limit.dto.LimitResponse;
import ru.limit.dto.OperationResponse;
import ru.limit.dto.ReserveRequest;
import ru.limit.exception.InsufficientLimitException;
import ru.limit.exception.LimitOperationException;
import ru.limit.exception.ValidationException;
import ru.limit.mapper.LimitMapper;
import ru.limit.model.Limit;
import ru.limit.model.ReservationStatus;
import ru.limit.model.ReservedLimit;
import ru.limit.repository.LimitRepository;
import ru.limit.repository.ReservedLimitRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class LimitService {
    private final LimitRepository limitRepository;
    private final ReservedLimitRepository reservedLimitRepository;
    private final LimitProperties limitProperties;
    private final LimitMapper limitMapper;

    public LimitService(LimitRepository limitRepository,
                        ReservedLimitRepository reservedLimitRepository,
                        LimitProperties limitProperties,
                        LimitMapper limitMapper) {
        this.limitRepository = limitRepository;
        this.reservedLimitRepository = reservedLimitRepository;
        this.limitProperties = limitProperties;
        this.limitMapper = limitMapper;
    }

    @Transactional
    public LimitResponse getLimit(Long userId) {
        validateUserId(userId);

        Limit limit = limitRepository.findByUserId(userId)
                .orElseGet(() -> createNewUserLimit(userId));

        BigDecimal reservedAmount = reservedLimitRepository.sumReservedAmountByUserId(userId);
        BigDecimal availableLimit = limit.getCurrentLimit().subtract(reservedAmount);

        return limitMapper.toLimitResponseWithReserved(limit, reservedAmount, availableLimit);
    }

    @Transactional
    public OperationResponse reserveLimit(ReserveRequest request) {
        validateUserId(request.userId());
        validateAmount(request.amount());
        validateOperationId(request.operationId());

        Optional<ReservedLimit> existingReservation = reservedLimitRepository.findByOperationId(request.operationId());
        if (existingReservation.isPresent()) {
            throw new ValidationException("Operation already reserved: " + request.operationId());
        }

        Limit limit = limitRepository.findByUserId(request.userId())
                .orElseGet(() -> createNewUserLimit(request.userId()));

        BigDecimal reservedAmount = reservedLimitRepository.sumReservedAmountByUserId(request.userId());
        BigDecimal availableLimit = limit.getCurrentLimit().subtract(reservedAmount);

        if (availableLimit.compareTo(request.amount()) < 0) {
            throw new InsufficientLimitException(request.userId(), availableLimit, request.amount());
        }

        ReservedLimit reservedLimit = new ReservedLimit(request.userId(), request.operationId(), request.amount());
        ReservedLimit savedReservation = reservedLimitRepository.save(reservedLimit);

        return limitMapper.toOperationResponse(
                savedReservation,
                true,
                "Limit reserved successfully"
        );
    }

    @Transactional
    public OperationResponse confirmOperation(String operationId) {
        validateOperationId(operationId);

        ReservedLimit reservedLimit = reservedLimitRepository.findByOperationId(operationId)
                .orElseThrow(() -> new ValidationException("Operation not found: " + operationId));

        if (reservedLimit.getStatus() != ReservationStatus.RESERVED) {
            throw new ValidationException("Operation already processed: " + operationId);
        }

        Limit limit = limitRepository.findByUserId(reservedLimit.getUserId())
                .orElseThrow(() -> new LimitOperationException("User limit not found for operation: " + operationId));

        BigDecimal newLimit = limit.getCurrentLimit().subtract(reservedLimit.getAmount());
        limit.setCurrentLimit(newLimit);
        limitRepository.save(limit);

        reservedLimit.setStatus(ReservationStatus.CONFIRMED);
        ReservedLimit updatedReservation = reservedLimitRepository.save(reservedLimit);

        return limitMapper.toOperationResponse(
                updatedReservation,
                true,
                "Operation confirmed successfully"
        );
    }

    @Transactional
    public OperationResponse cancelOperation(String operationId) {
        validateOperationId(operationId);

        ReservedLimit reservedLimit = reservedLimitRepository.findByOperationId(operationId)
                .orElseThrow(() -> new ValidationException("Operation not found: " + operationId));

        if (reservedLimit.getStatus() != ReservationStatus.RESERVED) {
            throw new ValidationException("Operation already processed: " + operationId);
        }

        reservedLimit.setStatus(ReservationStatus.CANCELLED);
        ReservedLimit updatedReservation = reservedLimitRepository.save(reservedLimit);

        return limitMapper.toOperationResponse(
                updatedReservation,
                true,
                "Operation cancelled successfully"
        );
    }

    @Transactional
    public OperationResponse decreaseLimit(Long userId, BigDecimal amount) {
        validateUserId(userId);
        validateAmount(amount);

        Limit limit = limitRepository.findByUserId(userId)
                .orElseGet(() -> createNewUserLimit(userId));

        BigDecimal reservedAmount = reservedLimitRepository.sumReservedAmountByUserId(userId);
        BigDecimal availableLimit = limit.getCurrentLimit().subtract(reservedAmount);

        if (availableLimit.compareTo(amount) < 0) {
            throw new InsufficientLimitException(userId, availableLimit, amount);
        }

        BigDecimal newLimit = limit.getCurrentLimit().subtract(amount);
        limit.setCurrentLimit(newLimit);
        limitRepository.save(limit);

        return limitMapper.toDecreaseOperationResponse(userId, amount, newLimit);
    }

    @Transactional
    public OperationResponse increaseLimit(Long userId, BigDecimal amount) {
        validateUserId(userId);
        validateAmount(amount);

        Limit limit = limitRepository.findByUserId(userId)
                .orElseGet(() -> createNewUserLimit(userId));

        BigDecimal newLimit = limit.getCurrentLimit().add(amount);
        limit.setCurrentLimit(newLimit);
        limitRepository.save(limit);

        return limitMapper.toIncreaseOperationResponse(userId, amount, newLimit);
    }

    @Transactional
    public void resetAllLimits() {
        try {
            limitRepository.updateAllLimits(limitProperties.defaultLimit());
            cleanupExpiredReservations();
        } catch (Exception e) {
            throw new LimitOperationException("Failed to reset all limits", e);
        }
    }

    @Transactional
    public void cleanupExpiredReservations() {
        LocalDateTime now = LocalDateTime.now();
        List<ReservedLimit> expiredReservations = reservedLimitRepository
                .findByStatusAndExpiresAtBefore(ReservationStatus.RESERVED, now);

        for (ReservedLimit reservation : expiredReservations) {
            reservation.setStatus(ReservationStatus.CANCELLED);
        }
        reservedLimitRepository.saveAll(expiredReservations);
    }

    private Limit createNewUserLimit(Long userId) {
        try {
            Limit newLimit = new Limit();
            newLimit.setUserId(userId);
            newLimit.setCurrentLimit(limitProperties.defaultLimit());
            return limitRepository.save(newLimit);
        } catch (Exception e) {
            throw new LimitOperationException("Failed to create limit for user: " + userId, e);
        }
    }

    private void validateUserId(Long userId) {
        if (userId == null) {
            throw new ValidationException("User ID cannot be null");
        }
        if (userId <= 0) {
            throw new ValidationException("User ID must be positive");
        }
    }

    private void validateAmount(BigDecimal amount) {
        if (amount == null) {
            throw new ValidationException("Amount cannot be null");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ValidationException("Amount must be positive");
        }
    }

    private void validateOperationId(String operationId) {
        if (operationId == null || operationId.trim().isEmpty()) {
            throw new ValidationException("Operation ID cannot be null or empty");
        }
    }
}