CREATE TABLE `device_upgrade` (
                                  `id` int(11) NOT NULL AUTO_INCREMENT,
                                  `dev_type` char(10) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '设备类型',
                                  `version_num` char(60) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '版本号',
                                  `check_code` char(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'md5',
                                  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
                                  `file_url` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件路径',
                                  `enable` tinyint(1) DEFAULT NULL COMMENT '是否启用',
                                  `forced_upgrade` tinyint(1) DEFAULT NULL COMMENT '是否强制升级',
                                  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '版本描述',
                                  `major_version` int(11) DEFAULT '0' COMMENT '涓荤増鏈�',
                                  `sub_version` int(11) DEFAULT '0' COMMENT '瀛愮増鏈�',
                                  `stage_version` int(11) DEFAULT '0' COMMENT '闃舵鐗堟湰',
                                  PRIMARY KEY (`id`),
                                  KEY `version_index` (`major_version`,`sub_version`,`stage_version`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;