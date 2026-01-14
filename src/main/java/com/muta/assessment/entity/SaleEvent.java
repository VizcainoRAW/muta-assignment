package com.muta.assessment.entity;

import com.muta.assessment.model.InventoryImpact;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;

import java.math.BigDecimal;

@Entity
@DiscriminatorValue("SALE")
public class SaleEvent extends OperationalEvent {

    @Transient
    public Long getCustomerId() {
        return ((Number) getEventData().get("customer_id")).longValue();
    }

    @Transient
    public void setCustomerId(Long customerId) {
        getEventData().put("customer_id", customerId);
    }

    @Transient
    public BigDecimal getSoldQuantity() {
        return new BigDecimal(getEventData().get("sold_quantity").toString());
    }

    @Transient
    public void setSoldQuantity(BigDecimal quantity) {
        getEventData().put("sold_quantity", quantity);
        setQuantity(quantity);
    }

    @Transient
    public BigDecimal getUnitPrice() {
        return new BigDecimal(getEventData().get("unit_price").toString());
    }

    @Transient
    public void setUnitPrice(BigDecimal price) {
        getEventData().put("unit_price", price);
    }

    @Override
    public InventoryImpact calculateInventoryImpact() {
        return InventoryImpact.OUT;
    }
}
