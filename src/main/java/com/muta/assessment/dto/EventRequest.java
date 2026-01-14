package com.muta.assessment.dto;


import java.time.Instant;

public sealed interface EventRequest permits
        CollectionRequest,
        TransferRequest,
        SaleRequest,
        AdjustmentRequest {

    Long licenseId();

    Long warehouseId();

    Instant eventTimestamp();

    Long createdBy();

    String eventType();
}
