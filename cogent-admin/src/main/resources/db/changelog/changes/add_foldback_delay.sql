ALTER TABLE `cogent-admin`.`foldback_source`
    ADD COLUMN `delay` int NULL DEFAULT 0 AFTER `stream_id`;