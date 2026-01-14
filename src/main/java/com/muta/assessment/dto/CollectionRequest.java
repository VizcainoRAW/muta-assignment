package com.muta.assessment.dto;

import org.antlr.v4.runtime.misc.NotNull;

import java.math.BigDecimal;
import java.time.Instant;

public record CollectionRequest(
        Long licenseId,

        Long warehouseId,

        Instant eventTimestamp,

        Long createdBy,

        Long supplierId,

        BigDecimal collectedQuantity
) implements EventRequest {

    @Override
    public String eventType() {
        return "collection";
    }
}
