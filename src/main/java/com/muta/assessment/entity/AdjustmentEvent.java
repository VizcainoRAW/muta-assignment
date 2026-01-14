package com.muta.assessment.entity;

import com.muta.assessment.model.InventoryImpact;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("ADJUSTMENT")
public class AdjustmentEvent extends OperationalEvent {

    @Transient
    public String getReason() {
        return (String) getEventData().get("reason");
    }

    @Transient
    public void setReason(String reason) {
        getEventData().put("reason", reason);
    }

    @Transient
    public BigDecimal getAdjustedQuantity() {
        return new BigDecimal(getEventData().get("adjusted_quantity").toString());
    }

    @Transient
    public void setAdjustedQuantity(BigDecimal quantity) {
        getEventData().put("adjusted_quantity", quantity);
        setQuantity(quantity.abs());
    }

    @Transient
    public String getAdjustmentType() {
        return (String) getEventData().get("adjustment_type");
    }

    @Transient
    public void setAdjustmentType(String type) {
        getEventData().put("adjustment_type", type);
    }

    @Override
    public InventoryImpact calculateInventoryImpact() {
        String type = getAdjustmentType();
        return "IN".equals(type) ? InventoryImpact.IN : InventoryImpact.OUT;
    }
}