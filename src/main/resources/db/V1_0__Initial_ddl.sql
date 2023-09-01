CREATE TABLE IF NOT EXISTS supplier_v1 (
    id uuid NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    identifier VARCHAR(128) NOT NULL,
    jwtid VARCHAR(255) NOT NULL,
    updated_by VARCHAR(32) NOT NULL,
    created_by VARCHAR(32) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    unique(identifier)
);

CREATE TABLE IF NOT EXISTS product_transfer_v1 (
    transfer_id uuid NOT NULL PRIMARY KEY,
    supplier_id uuid NOT NULL,
    supplier_ref VARCHAR(255) NOT NULL,
    md5 VARCHAR(32) NOT NULL,
    json_payload JSONB NOT NULL,
    transfer_status VARCHAR(32) NOT NULL,
    message TEXT,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(md5)
);

CREATE INDEX product_transfer_v1_supplierId_supplierRef_idx ON product_transfer_v1(supplier_id, supplier_ref);

CREATE TABLE IF NOT EXISTS product_state_v1 (
    id uuid NOT NULL PRIMARY KEY,
    transfer_id UUID NOT NULL,
    supplier_id uuid NOT NULL,
    supplier_ref VARCHAR(255) NOT NULL,
    product_dto JSONB NOT NULL,
    admin_status VARCHAR(32),
    admin_message TEXT,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (supplier_id, supplier_ref)
);

CREATE INDEX product_state_transferId_idx ON product_state_v1(transfer_id);

CREATE TABLE IF NOT EXISTS series_state_v1(
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    supplier_id UUID NOT NULL,
    name VARCHAR(1024) NOT NULL,
    message TEXT,
    status VARCHAR(32) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (name, supplier_id)
);

