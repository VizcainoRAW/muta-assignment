package com.muta.assessment.entity;

import com.muta.assessment.model.InventoryImpact;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "operational_events")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "event_type", discriminatorType = DiscriminatorType.STRING)
public abstract class OperationalEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "license_id", nullable = false)
    private Long licenseId;

    @Column(name = "warehouse_id", nullable = false)
    private Long warehouseId;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "event_data", columnDefinition = "jsonb")
    private Map eventData = new HashMap();

    @Column(name = "inventory_impact", nullable = false)
    @Enumerated(EnumType.STRING)
    private InventoryImpact inventoryImpact;

    @Column(name = "quantity", nullable = false)
    private BigDecimal quantity;

    @Column(name = "item_id")
    private Long itemId;

    @Version
    private Integer version;

    public abstract InventoryImpact calculateInventoryImpact();

    @PrePersist
    protected void prePersist() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        inventoryImpact = calculateInventoryImpact();
    }
}
