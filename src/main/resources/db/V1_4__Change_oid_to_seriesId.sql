ALTER TABLE media_transfer_v1 RENAME COLUMN oid TO series_id;
ALTER TABLE media_transfer_v1 DROP COLUMN supplier_ref;