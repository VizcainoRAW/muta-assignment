package com.muta.assessment.entity;

import com.muta.assessment.model.InventoryImpact;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("COLLECTION")
public class CollectionEvent extends OperationalEvent {

    @Transient
    public Long getSupplierId() {
        return ((Number) getEventData().get("supplier_id")).longValue();
    }

    @Transient
    public void setSupplierId(Long supplierId) {
        getEventData().put("supplier_id", supplierId);
    }

    @Transient
    public BigDecimal getCollectedQuantity() {
        return new BigDecimal(getEventData().get("collected_quantity").toString());
    }

    @Transient
    public void setCollectedQuantity(BigDecimal quantity) {
        getEventData().put("collected_quantity", quantity);
        setQuantity(quantity);
    }

    @Override
    public InventoryImpact calculateInventoryImpact() {
        return InventoryImpact.IN;
    }
}