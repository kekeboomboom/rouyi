CREATE TABLE `device_gps_cur` (
                                  `sn` varchar(30) COLLATE utf8mb4_unicode_ci NOT NULL,
                                  `lat` decimal(10,7) NOT NULL COMMENT 'latitude',
                                  `lon` decimal(10,7) NOT NULL COMMENT 'longitude',
                                  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;