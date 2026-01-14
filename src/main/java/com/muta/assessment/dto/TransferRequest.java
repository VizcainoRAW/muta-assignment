package com.muta.assessment.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TransferRequest(
        Long licenseId,

        Long warehouseId,

        Instant eventTimestamp,

        Long createdBy,

        Long sourceWarehouseId,

        Long targetWarehouseId,

        BigDecimal transferredQuantity
) implements EventRequest {

    @Override
    public String eventType() {
        return "transfer";
    }

    // Validación custom
    public TransferRequest {
        if (sourceWarehouseId != null && targetWarehouseId != null
                && sourceWarehouseId.equals(targetWarehouseId)) {
            throw new IllegalArgumentException("Source and target warehouses must be different");
        }
    }
}
