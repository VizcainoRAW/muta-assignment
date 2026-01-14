package com.muta.assessment.entity;

import com.muta.assessment.model.InventoryImpact;
import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "events")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "event_type", discriminatorType = DiscriminatorType.STRING)
public abstract class OperationalEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_id", nullable = false)
    private Long licenseId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "event_type", insertable = false, updatable = false)
    private String eventType;

    @Column(name = "event_timestamp", nullable = false)
    private Instant eventTimestamp;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt = Instant.now();

    public abstract InventoryImpact calculateImpact();

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = Instant.now();
        }
        if (eventTimestamp == null) {
            eventTimestamp = Instant.now();
        }
    }

    public Long getWarehouseId() {
        return warehouseId;
    }
}
