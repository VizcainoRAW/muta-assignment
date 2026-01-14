package com.muta.assessment.model;

import java.math.BigDecimal;

public class InventoryImpact {
    private final Long warehouseId;
    private final BigDecimal quantity;
    private final ImpactType type;

    public InventoryImpact(Long warehouseId, BigDecimal quantity, ImpactType type) {
        this.warehouseId = warehouseId;
        this.quantity = quantity;
        this.type = type;
    }

    public enum ImpactType {
        IN, OUT, NEUTRAL
    }
}