ALTER TABLE `cogent-admin`.`backpack`
    ADD COLUMN `state` varchar(20) NULL COMMENT '设备在线离线' AFTER `foldback_stream_id`;