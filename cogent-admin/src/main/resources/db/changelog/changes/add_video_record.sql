CREATE TABLE `video_record` (
                                `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
                                `file_alias` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '文件别名，如果要修改文件名字，则去修改文件别名。此字段默认为file_name',
                                `file_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '11-26-52-0.mp4',
                                `file_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '/opt/java-server/mediakit/www/record/live/C10821A0212/2023-10-24/11-26-52-0.mp4',
                                `file_size` bigint(20) DEFAULT NULL COMMENT '14832817',
                                `folder` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '/opt/java-server/mediakit/www/record/live/C10821A0212/',
                                `media_server_id` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'your_server_id',
                                `start_time` datetime DEFAULT NULL COMMENT '1698118012000',
                                `stream` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'C10821A0212',
                                `app` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'live',
                                `url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'record/live/C10821A0212/2023-10-24/11-26-52-0.mp4',
                                `vhost` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '__defaultVhost__',
                                `snap_path` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT 'snap/C10821A0212/2023-10-24/11-26-52-0.jpeg',
                                PRIMARY KEY (`id`),
                                KEY `time_stream_index` (`start_time`,`stream`) USING BTREE COMMENT '根据时间查询某个设备'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;