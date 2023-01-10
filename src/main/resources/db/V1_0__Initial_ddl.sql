CREATE EXTENSION pgcrypto;

CREATE TABLE IF NOT EXISTS supplier_v1 (
    id uuid NOT NULL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    info JSONB NOT NULL,
    identifier VARCHAR(128) NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    unique(name),
    unique(identifier)
);

CREATE TABLE IF NOT EXISTS transferstate_v1 (
    id uuid NOT NULL PRIMARY KEY,
    supplier_id uuid NOT NULL,
    transfer_ref VARCHAR(255) NOT NULL,
    md5 VARCHAR(32) NOT NULL,
    items INTEGER NOT NULL,
    type VARCHAR(32) NOT NULL,
    json_payload JSONB NOT NULL,
    status VARCHAR(32) NOT NULL,
    message VARCHAR(255),
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(md5),
    UNIQUE(supplierId, transfer_ref),
    CONSTRAINT fk_supplier_transferstate FOREIGN KEY (supplier_id) REFERENCES supplier_v1(id)
);

CREATE TABLE IF NOT EXISTS productstate_v1 (
    id uuid NOT NULL PRIMARY KEY,
    supplier_id uuid NOT NULL,
    supplier_ref VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    json_payload JSONB NOT NULL,
    version BIGINT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (supplier_id, supplier_ref)
);

CREATE TABLE IF NOT EXISTS adminstatus_v1 (
    id uuid NOT NULL PRIMARY KEY,
    supplier_id uuid NOT NULL,
    supplier_ref VARCHAR(255) NOT NULL,
    status VARCHAR(32) NOT NULL,
    message VARCHAR(512),
    version BIGINT NOT NULL,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(supplier_id, supplier_ref)
);

