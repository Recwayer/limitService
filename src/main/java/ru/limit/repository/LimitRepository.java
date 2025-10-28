package ru.limit.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.limit.model.Limit;

import java.math.BigDecimal;
import java.util.Optional;

@Repository
public interface LimitRepository extends JpaRepository<Limit, Long> {
    Optional<Limit> findByUserId(Long userId);

    @Modifying
    @Query("UPDATE Limit l SET l.currentLimit = :newLimit")
    void updateAllLimits(@Param("newLimit") BigDecimal newLimit);
}
