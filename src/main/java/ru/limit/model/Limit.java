package ru.limit.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "user_limits")
public class Limit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Column(name = "current_limit", precision = 12, scale = 2, nullable = false)
    private BigDecimal currentLimit;

    public Limit() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public BigDecimal getCurrentLimit() {
        return currentLimit;
    }

    public void setCurrentLimit(BigDecimal currentLimit) {
        this.currentLimit = currentLimit;
    }
}
