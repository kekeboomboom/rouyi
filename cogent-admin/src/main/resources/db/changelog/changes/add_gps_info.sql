CREATE TABLE `gps_info` (
                                  `sn` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `lat` decimal(10,7) NOT NULL COMMENT 'latitude',
                                  `lon` decimal(10,7) NOT NULL COMMENT 'longitude',
                                  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                  KEY `timestamp_sn_index` (`create_time`,`sn`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;