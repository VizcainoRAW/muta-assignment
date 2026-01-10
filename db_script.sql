CREATE TABLE operational_events (
    id BIGSERIAL PRIMARY KEY,
    license_id BIGINT NOT NULL,
    warehouse_id BIGINT NOT NULL,
    event_type VARCHAR(20) NOT NULL CHECK (event_type IN ('COLLECTION', 'TRANSFER', 'SALE', 'ADJUSTMENT')),
    event_timestamp TIMESTAMP NOT NULL,
    created_by VARCHAR(100) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    event_data JSONB NOT NULL,

    inventory_impact VARCHAR(10) NOT NULL CHECK (inventory_impact IN ('IN', 'OUT', 'NEUTRAL')),
    quantity DECIMAL(15, 3) NOT NULL,
    item_id BIGINT,

    version INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX idx_events_inventory_query 
ON operational_events(license_id, warehouse_id, item_id, inventory_impact) 
WHERE inventory_impact != 'NEUTRAL';

CREATE INDEX idx_events_timestamp 
ON operational_events(event_timestamp DESC);

CREATE INDEX idx_events_license_warehouse 
ON operational_events(license_id, warehouse_id);

CREATE INDEX idx_events_type 
ON operational_events(event_type);

CREATE INDEX idx_events_data_gin 
ON operational_events USING GIN (event_data);

CREATE MATERIALIZED VIEW inventory_snapshot AS
SELECT 
    license_id,
    warehouse_id,
    item_id,
    SUM(CASE 
        WHEN inventory_impact = 'IN' THEN quantity
        WHEN inventory_impact = 'OUT' THEN -quantity
        ELSE 0
    END) as current_stock
FROM operational_events
WHERE item_id IS NOT NULL
GROUP BY license_id, warehouse_id, item_id;

CREATE UNIQUE INDEX idx_inventory_snapshot_pk 
ON inventory_snapshot(license_id, warehouse_id, item_id);