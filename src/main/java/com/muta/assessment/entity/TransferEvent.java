package com.muta.assessment.entity;

import com.muta.assessment.model.InventoryImpact;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("TRANSFER")
public class TransferEvent extends OperationalEvent {

    @Transient
    public Long getSourceWarehouseId() {
        return ((Number) getEventData().get("source_warehouse_id")).longValue();
    }

    @Transient
    public void setSourceWarehouseId(Long warehouseId) {
        getEventData().put("source_warehouse_id", warehouseId);
    }

    @Transient
    public Long getTargetWarehouseId() {
        return ((Number) getEventData().get("target_warehouse_id")).longValue();
    }

    @Transient
    public void setTargetWarehouseId(Long warehouseId) {
        getEventData().put("target_warehouse_id", warehouseId);
    }

    @Transient
    public BigDecimal getTransferredQuantity() {
        return new BigDecimal(getEventData().get("transferred_quantity").toString());
    }

    @Transient
    public void setTransferredQuantity(BigDecimal quantity) {
        getEventData().put("transferred_quantity", quantity);
        setQuantity(quantity);
    }

    @Override
    public InventoryImpact calculateInventoryImpact() {
        // La transferencia genera DOS eventos:
        // - OUT en bodega origen
        // - IN en bodega destino
        // Este evento representa el OUT
        return InventoryImpact.OUT;
    }
}