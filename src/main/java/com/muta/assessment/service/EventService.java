package com.muta.assessment.service;

import com.muta.assessment.dto.*;
import com.muta.assessment.entity.*;
import com.muta.assessment.exception.InsufficientStockException;
import com.muta.assessment.exception.InvalidEventException;
import com.muta.assessment.exception.LicenseNotFoundException;
import com.muta.assessment.model.InventoryImpact;
import com.muta.assessment.repository.EventRepository;
import com.muta.assessment.repository.LicenseRepository;
import com.muta.assessment.repository.WarehouseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;

@Service
public class EventService {

    private final EventRepository eventRepository;
    private final LicenseRepository licenseRepository;
    private final WarehouseRepository warehouseRepository;
    private final InventoryService inventoryService;

    public EventService(
            EventRepository eventRepository,
            LicenseRepository licenseRepository,
            WarehouseRepository warehouseRepository,
            InventoryService inventoryService
    ) {
        this.eventRepository = eventRepository;
        this.licenseRepository = licenseRepository;
        this.warehouseRepository = warehouseRepository;
        this.inventoryService = inventoryService;
    }

    @Transactional
    public OperationalEvent createEvent(EventRequest request) {
        // 1. Validar licencia
        validateLicense(request.licenseId());

        // 2. Crear evento según tipo
        OperationalEvent event = mapToEntity(request);

        // 3. Validar inventario para eventos OUT
        validateInventoryImpact(event);

        // 4. Persistir evento
        OperationalEvent savedEvent = eventRepository.save(event);

        // 5. Aplicar impacto en inventario
        inventoryService.applyImpact(savedEvent);

        return savedEvent;
    }

    private void validateLicense(Long licenseId) {
        licenseRepository.findById(licenseId)
                .filter(license -> "active".equals(license.getStatus()))
                .orElseThrow(() -> new LicenseNotFoundException(licenseId));
    }

    private OperationalEvent mapToEntity(EventRequest request) {
        OperationalEvent event = switch (request) {
            case CollectionRequest r -> {
                CollectionEvent e = new CollectionEvent();
                e.setSupplierId(r.supplierId());
                e.setCollectedQuantity(r.collectedQuantity());
                yield e;
            }
            case TransferRequest r -> {
                TransferEvent e = new TransferEvent();
                e.setSourceWarehouseId(r.sourceWarehouseId());
                e.setTargetWarehouseId(r.targetWarehouseId());
                e.setTransferredQuantity(r.transferredQuantity());
                yield e;
            }
            case SaleRequest r -> {
                SaleEvent e = new SaleEvent();
                e.setCustomerId(r.customerId());
                e.setSoldQuantity(r.soldQuantity());
                e.setUnitPrice(r.unitPrice());
                yield e;
            }
            case AdjustmentRequest r -> {
                AdjustmentEvent e = new AdjustmentEvent();
                e.setReason(r.reason());
                e.setAdjustedQuantity(r.adjustedQuantity());
                yield e;
            }
        };

        // Campos comunes
        event.setLicenseId(request.licenseId());
        event.setWarehouseId(request.warehouseId());
        event.setCreatedBy(request.createdBy());
        event.setEventTimestamp(
                request.eventTimestamp() != null
                        ? request.eventTimestamp()
                        : Instant.now()
        );

        return event;
    }

    private void validateInventoryImpact(OperationalEvent event) {
        InventoryImpact impact = event.calculateImpact();

        // Solo validar para eventos OUT
        if (impact.getType() == InventoryImpact.ImpactType.OUT) {
            if (!inventoryService.hasSufficientInventory(
                    event.getLicenseId(),
                    impact.getWarehouseId(),
                    impact.getQuantity()
            )) {
                throw new InsufficientStockException(
                        "Insufficient inventory in warehouse " + impact.getWarehouseId()
                );
            }
        }

        // Para transferencias, validar ambos extremos
        if (event instanceof TransferEvent transfer) {
            validateTransferImpact(transfer);
        }
    }

    private void validateTransferImpact(TransferEvent transfer) {
        // Validar que bodega origen tenga suficiente inventario
        if (!inventoryService.hasSufficientInventory(
                transfer.getLicenseId(),
                transfer.getSourceWarehouseId(),
                transfer.getTransferredQuantity()
        )) {
            throw new InsufficientStockException(
                    "Insufficient inventory in source warehouse " + transfer.getSourceWarehouseId()
            );
        }

        // Validar que bodega destino exista y pertenezca a la licencia
        warehouseRepository.findByIdAndLicenseId(
                transfer.getTargetWarehouseId(),
                transfer.getLicenseId()
        ).orElseThrow(() -> new InvalidEventException(
                "Target warehouse " + transfer.getTargetWarehouseId() +
                        " not found or doesn't belong to license " + transfer.getLicenseId()
        ));
    }
}