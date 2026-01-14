package com.muta.assessment.entity;

import com.muta.assessment.model.InventoryImpact;
import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "sale_events")
@DiscriminatorValue("sale")
public class SaleEvent extends OperationalEvent {

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @Column(name = "sold_quantity", nullable = false, precision = 12, scale = 3)
    private BigDecimal soldQuantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Override
    public InventoryImpact calculateImpact() {
        return new InventoryImpact(
                getWarehouseId(),
                soldQuantity,
                InventoryImpact.ImpactType.OUT
        );
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public void setSoldQuantity(BigDecimal soldQuantity) {
        this.soldQuantity = soldQuantity;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
}