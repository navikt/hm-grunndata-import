CREATE TABLE media_meta_transfer_v1 (
    transfer_id UUID PRIMARY KEY,
    uri VARCHAR(2048) NOT NULL,
    supplier_id UUID NOT NULL,
    series_id UUID NOT NULL,
    text Text,
    priority INT NOT NULL,
    status VARCHAR(32) NOT NULL,
    transfer_status VARCHAR(32) NOT NULL,
    message TEXT,
    created TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP

)