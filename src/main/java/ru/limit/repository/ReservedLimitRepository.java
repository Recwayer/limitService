package ru.limit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.limit.model.ReservationStatus;
import ru.limit.model.ReservedLimit;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservedLimitRepository extends JpaRepository<ReservedLimit, Long> {
    Optional<ReservedLimit> findByOperationId(String operationId);

    List<ReservedLimit> findByStatusAndExpiresAtBefore(ReservationStatus status, LocalDateTime expiresAt);

    @Query("SELECT COALESCE(SUM(r.amount), 0) FROM ReservedLimit r WHERE r.userId = :userId AND r.status = 'RESERVED'")
    BigDecimal sumReservedAmountByUserId(@Param("userId") Long userId);
}
