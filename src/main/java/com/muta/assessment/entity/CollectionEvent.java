package com.muta.assessment.entity;

import com.muta.assessment.model.InventoryImpact;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "collection_events")
@DiscriminatorValue("collection")
public class CollectionEvent extends OperationalEvent {

    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;

    @Column(name = "collected_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal collectedQuantity;

    @Override
    public InventoryImpact calculateImpact() {
        return new InventoryImpact(
                getWarehouseId(),
                collectedQuantity,
                InventoryImpact.ImpactType.IN
        );
    }

    public void setSupplierId(Long supplierId) {
        this.supplierId = supplierId;
    }

    public void setCollectedQuantity(BigDecimal collectedQuantity) {
        this.collectedQuantity = collectedQuantity;
    }
}