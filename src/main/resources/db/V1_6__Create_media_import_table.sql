CREATE TABLE media_import_v1(
    id uuid NOT NULL PRIMARY KEY,
    uri VARCHAR(2048) NOT NULL,
    supplier_id uuid NOT NULL,
    series_id uuid NOT NULL,
    transfer_id uuid,
    source_uri VARCHAR(2048),
    filename VARCHAR(255),
    text TEXT,
    md5 VARCHAR(32),
    source_type VARCHAR(32) NOT NULL,
    priority INT NOT NULL
);
