ALTER TABLE `cogent-admin`.`backpack`
    MODIFY COLUMN `sn` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '背包序列号。如果是手机，则是AndroidID' AFTER `id`,
    MODIFY COLUMN `dev_type` char(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '设备类型。如果是手机，则是手机型号加厂商' AFTER `dev_name`,
    ADD COLUMN `android_version` varchar(50) NULL COMMENT '手机Android系统版本' AFTER `state`;