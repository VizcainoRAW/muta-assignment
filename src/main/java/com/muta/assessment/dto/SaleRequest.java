package com.muta.assessment.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record SaleRequest(
        Long licenseId,

        Long warehouseId,

        Instant eventTimestamp,

        Long createdBy,

        Long customerId,

        BigDecimal soldQuantity,

        BigDecimal unitPrice
) implements EventRequest {

    @Override
    public String eventType() {
        return "sale";
    }
}