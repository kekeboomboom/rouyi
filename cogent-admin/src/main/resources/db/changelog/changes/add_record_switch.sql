ALTER TABLE `cogent-admin`.`backpack`
ADD COLUMN `record_switch` tinyint(1) NULL COMMENT '录像开关' AFTER `android_version`;