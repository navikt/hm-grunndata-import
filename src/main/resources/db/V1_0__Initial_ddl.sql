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

CREATE TABLE IF NOT EXISTS product_import_v1 (
    id uuid NOT NULL PRIMARY KEY,
    transfer_id UUID NOT NULL,
    supplier_id uuid NOT NULL,
    supplier_ref VARCHAR(255) NOT NULL,
    product_dto JSONB NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL,
    UNIQUE (supplier_id, supplier_ref)
);

CREATE INDEX product_import_v1_transferId_idx ON product_import_v1(transfer_id);

CREATE TABLE IF NOT EXISTS series_transfer_v1 (
    transfer_id uuid NOT NULL PRIMARY KEY,
    series_id UUID NOT NULL,
    supplier_id uuid NOT NULL,
    md5 VARCHAR(32) NOT NULL,
    json_payload JSONB NOT NULL,
    transfer_status VARCHAR(32) NOT NULL,
    message TEXT,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(md5)
);

CREATE INDEX series_transfer_v1_supplier_id_series_id_idx ON series_transfer_v1(supplier_id, series_id);

CREATE TABLE IF NOT EXISTS series_import_v1 (
    series_id UUID NOT NULL PRIMARY KEY,
    identifier VARCHAR(255) NOT NULL,
    supplier_id UUID NOT NULL,
    transfer_id UUID NOT NULL,
    name VARCHAR(255) NOT NULL,
    message TEXT,
    status VARCHAR(32) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL,
    UNIQUE (name, supplier_id),
    UNIQUE (identifier)
);

CREATE TABLE IF NOT EXISTS product_admin_state_v1 (
    id uuid NOT NULL PRIMARY KEY,
    transfer_id UUID NOT NULL,
    supplier_id uuid NOT NULL,
    supplier_ref VARCHAR(255) NOT NULL,
    product_status VARCHAR(32) NOT NULL,
    admin_status VARCHAR(32),
    admin_message TEXT,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT NOT NULL,
    UNIQUE (supplier_id, supplier_ref)
)