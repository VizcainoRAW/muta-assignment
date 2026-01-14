CREATE TABLE licenses (
                          id BIGSERIAL PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          status VARCHAR(20) NOT NULL DEFAULT 'active' CHECK (status IN ('active', 'inactive')),
                          created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE users (
                       id BIGSERIAL PRIMARY KEY,
                       license_id BIGINT NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       name VARCHAR(255) NOT NULL,
                       created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                       CONSTRAINT fk_license FOREIGN KEY (license_id) REFERENCES licenses(id)
);

CREATE TABLE warehouses (
                            id BIGSERIAL PRIMARY KEY,
                            license_id BIGINT NOT NULL,
                            name VARCHAR(255) NOT NULL,
                            created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                            CONSTRAINT fk_license FOREIGN KEY (license_id) REFERENCES licenses(id)
);

CREATE INDEX idx_users_license ON users(license_id);
CREATE INDEX idx_warehouses_license ON warehouses(license_id);

CREATE TABLE events (
                        id BIGSERIAL PRIMARY KEY,
                        license_id BIGINT NOT NULL,
                        warehouse_id BIGINT NOT NULL,
                        event_type VARCHAR(20) NOT NULL CHECK (event_type IN ('collection', 'transfer', 'sale', 'adjustment')),
                        event_timestamp TIMESTAMPTZ NOT NULL,
                        created_by BIGINT NOT NULL,
                        created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

                        CONSTRAINT fk_license FOREIGN KEY (license_id) REFERENCES licenses(id),
                        CONSTRAINT fk_warehouse FOREIGN KEY (warehouse_id) REFERENCES warehouses(id),
                        CONSTRAINT fk_user FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE collection_events (
                                   event_id BIGINT PRIMARY KEY,
                                   supplier_id BIGINT NOT NULL,
                                   collected_quantity DECIMAL(12,3) NOT NULL CHECK (collected_quantity > 0),

                                   CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

CREATE TABLE transfer_events (
                                 event_id BIGINT PRIMARY KEY,
                                 source_warehouse_id BIGINT NOT NULL,
                                 target_warehouse_id BIGINT NOT NULL,
                                 transferred_quantity DECIMAL(12,3) NOT NULL CHECK (transferred_quantity > 0),

                                 CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE,
                                 CONSTRAINT fk_source_warehouse FOREIGN KEY (source_warehouse_id) REFERENCES warehouses(id),
                                 CONSTRAINT fk_target_warehouse FOREIGN KEY (target_warehouse_id) REFERENCES warehouses(id),
                                 CONSTRAINT chk_different_warehouses CHECK (source_warehouse_id != target_warehouse_id)
    );

CREATE TABLE sale_events (
                             event_id BIGINT PRIMARY KEY,
                             customer_id BIGINT NOT NULL,
                             sold_quantity DECIMAL(12,3) NOT NULL CHECK (sold_quantity > 0),
                             unit_price DECIMAL(12,2) NOT NULL CHECK (unit_price >= 0),

                             CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

CREATE TABLE adjustment_events (
                                   event_id BIGINT PRIMARY KEY,
                                   reason TEXT NOT NULL,
                                   adjusted_quantity DECIMAL(12,3) NOT NULL CHECK (adjusted_quantity != 0),

    CONSTRAINT fk_event FOREIGN KEY (event_id) REFERENCES events(id) ON DELETE CASCADE
);

-- optimizacion
CREATE INDEX idx_events_license_timestamp ON events(license_id, event_timestamp DESC);
CREATE INDEX idx_events_warehouse_timestamp ON events(warehouse_id, event_timestamp DESC);
CREATE INDEX idx_events_type_timestamp ON events(event_type, event_timestamp DESC);
CREATE INDEX idx_events_license_warehouse ON events(license_id, warehouse_id, event_timestamp DESC);