package com.muta.assessment.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record AdjustmentRequest(
        Long licenseId,

        Long warehouseId,

        Instant eventTimestamp,

        Long createdBy,

        String reason,

        BigDecimal adjustedQuantity
) implements EventRequest {

    @Override
    public String eventType() {
        return "adjustment";
    }

    public AdjustmentRequest {
        if (adjustedQuantity != null && adjustedQuantity.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("Adjusted quantity cannot be zero");
        }
    }
}
