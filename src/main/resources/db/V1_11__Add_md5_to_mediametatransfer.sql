ALTER TABLE media_meta_transfer_v1 ADD COLUMN md5 VARCHAR(32) NOT NULL DEFAULT '';
CREATE UNIQUE INDEX media_meta_transfer_v1_supplier_id_md5_unique_idx ON media_meta_transfer_v1 (supplier_id, md5);