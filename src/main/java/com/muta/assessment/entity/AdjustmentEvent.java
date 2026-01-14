package com.muta.assessment.entity;

import com.muta.assessment.model.InventoryImpact;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "adjustment_events")
@DiscriminatorValue("adjustment")
public class AdjustmentEvent extends OperationalEvent {

    @Column(name = "reason", nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "adjusted_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal adjustedQuantity;

    @Override
    public InventoryImpact calculateImpact() {
        InventoryImpact.ImpactType type = adjustedQuantity.compareTo(BigDecimal.ZERO) > 0
                ? InventoryImpact.ImpactType.IN
                : InventoryImpact.ImpactType.OUT;

        return new InventoryImpact(
                getWarehouseId(),
                adjustedQuantity.abs(),
                type
        );
    }
}