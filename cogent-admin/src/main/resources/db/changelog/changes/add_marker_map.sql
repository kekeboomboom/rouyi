CREATE TABLE `gps_marker` (
                              `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                              `lat` decimal(10,7) NOT NULL,
                              `lon` decimal(10,7) NOT NULL,
                              `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
                              PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;