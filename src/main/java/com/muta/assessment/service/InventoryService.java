package com.muta.assessment.service;

import com.muta.assessment.entity.OperationalEvent;
import com.muta.assessment.entity.TransferEvent;
import com.muta.assessment.model.InventoryImpact;
import com.muta.assessment.repository.InventoryRepository;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import org.slf4j.Logger;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    public InventoryService(InventoryRepository inventoryRepository){
        this.inventoryRepository = inventoryRepository;
    }

    public boolean hasSufficientInventory(
            Long licenseId,
            Long warehouseId,
            BigDecimal requiredQuantity
    ) {
        BigDecimal currentInventory = inventoryRepository.getCurrentInventory(
                licenseId,
                warehouseId
        );

        boolean sufficient = currentInventory.compareTo(requiredQuantity) >= 0;

        log.debug(
                "Inventory check - License: {}, Warehouse: {}, Current: {}, Required: {}, Sufficient: {}",
                licenseId, warehouseId, currentInventory, requiredQuantity, sufficient
        );

        return sufficient;
    }

    public void applyImpact(OperationalEvent event) {
        if (event instanceof TransferEvent transfer) {
            log.info(
                    "Transfer applied - License: {}, Source: {} (-{}), Target: {} (+{})",
                    event.getLicenseId(),
                    transfer.getSourceWarehouseId(),
                    transfer.getTransferredQuantity(),
                    transfer.getTargetWarehouseId(),
                    transfer.getTransferredQuantity()
            );
        } else {
            InventoryImpact impact = event.calculateImpact();
            log.info(
                    "Event applied - License: {}, Warehouse: {}, Type: {}, Quantity: {} {}",
                    event.getLicenseId(),
                    event.getWarehouseId(),
                    event.getEventType(),
                    impact.getQuantity(),
                    impact.getType()
            );
        }
    }

    public BigDecimal getWarehouseInventory(Long licenseId, Long warehouseId) {
        return inventoryRepository.getCurrentInventory(licenseId, warehouseId);
    }
}
