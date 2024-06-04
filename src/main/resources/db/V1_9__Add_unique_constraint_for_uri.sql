ALTER TABLE media_import_v1 add constraint unique_media_import_v1_uri unique (uri);
ALTER TABLE media_transfer_v1 add column media_type VARCHAR(32) NOT NULL DEFAULT 'IMAGE';