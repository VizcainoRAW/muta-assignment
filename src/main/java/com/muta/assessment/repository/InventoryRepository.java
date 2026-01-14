package com.muta.assessment.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;

@Repository
public interface InventoryRepository {

    @Query(value = """
        WITH inventory_impacts AS (
            SELECT 
                e.license_id,
                e.warehouse_id,
                CASE 
                    WHEN e.event_type = 'collection' THEN ce.collected_quantity
                    WHEN e.event_type = 'sale' THEN -se.sold_quantity
                    WHEN e.event_type = 'adjustment' THEN ae.adjusted_quantity
                    WHEN e.event_type = 'transfer' THEN 
                        CASE 
                            WHEN te.source_warehouse_id = e.warehouse_id THEN -te.transferred_quantity
                            WHEN te.target_warehouse_id = e.warehouse_id THEN te.transferred_quantity
                            ELSE 0
                        END
                    ELSE 0
                END AS quantity_impact
            FROM events e
            LEFT JOIN collection_events ce ON e.id = ce.event_id AND e.event_type = 'collection'
            LEFT JOIN sale_events se ON e.id = se.event_id AND e.event_type = 'sale'
            LEFT JOIN adjustment_events ae ON e.id = ae.event_id AND e.event_type = 'adjustment'
            LEFT JOIN transfer_events te ON e.id = te.event_id AND e.event_type = 'transfer'
            WHERE e.license_id = :licenseId 
              AND e.warehouse_id = :warehouseId
        )
        SELECT COALESCE(SUM(quantity_impact), 0) AS current_inventory
        FROM inventory_impacts
        """,
            nativeQuery = true)
    BigDecimal getCurrentInventory(
            @Param("licenseId") Long licenseId,
            @Param("warehouseId") Long warehouseId
    );
}