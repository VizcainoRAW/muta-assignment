package com.muta.assessment.entity;

import com.muta.assessment.model.InventoryImpact;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "transfer_events")
@DiscriminatorValue("transfer")
public class TransferEvent extends OperationalEvent {

    @Column(name = "source_warehouse_id", nullable = false)
    private Long sourceWarehouseId;

    @Column(name = "target_warehouse_id", nullable = false)
    private Long targetWarehouseId;

    @Column(name = "transferred_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal transferredQuantity;

    @Override
    public InventoryImpact calculateImpact() {
        return new InventoryImpact(
                sourceWarehouseId,
                transferredQuantity,
                InventoryImpact.ImpactType.OUT
        );
    }

    public List<InventoryImpact> calculateBothImpacts() {
        return Arrays.asList(
                new InventoryImpact(sourceWarehouseId, transferredQuantity, InventoryImpact.ImpactType.OUT),
                new InventoryImpact(targetWarehouseId, transferredQuantity, InventoryImpact.ImpactType.IN)
        );
    }

    public Long getSourceWarehouseId() {
        return sourceWarehouseId;
    }

    public void setSourceWarehouseId(Long sourceWarehouseId) {
        this.sourceWarehouseId = sourceWarehouseId;
    }

    public Long getTargetWarehouseId() {
        return targetWarehouseId;
    }

    public void setTargetWarehouseId(Long targetWarehouseId) {
        this.targetWarehouseId = targetWarehouseId;
    }

    public BigDecimal getTransferredQuantity() {
        return transferredQuantity;
    }

    public void setTransferredQuantity(BigDecimal transferredQuantity) {
        this.transferredQuantity = transferredQuantity;
    }
}