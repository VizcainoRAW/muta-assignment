package com.muta.assessment.service;

import com.muta.assessment.exception.InsufficientStockException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class InventoryService {

    @PersistenceContext
    private EntityManager entityManager;

    public void validateStockAvailability(
            Long licenseId,
            Long warehouseId,
            Long itemId,
            BigDecimal requestedQuantity) {

        String sql = """
            SELECT COALESCE(SUM(
                CASE 
                    WHEN inventory_impact = 'IN' THEN quantity
                    WHEN inventory_impact = 'OUT' THEN -quantity
                    ELSE 0
                END
            ), 0) as current_stock
            FROM operational_events
            WHERE license_id = :licenseId
              AND warehouse_id = :warehouseId
              AND item_id = :itemId
              AND inventory_impact != 'NEUTRAL'
            """;

        BigDecimal currentStock = (BigDecimal) entityManager
                .createNativeQuery(sql)
                .setParameter("licenseId", licenseId)
                .setParameter("warehouseId", warehouseId)
                .setParameter("itemId", itemId)
                .getSingleResult();

        if (currentStock.compareTo(requestedQuantity) < 0) {
            throw new InsufficientStockException(
                    String.format(
                            "Insufficient stock. Available: %s, Requested: %s",
                            currentStock,
                            requestedQuantity
                    )
            );
        }
    }
}
