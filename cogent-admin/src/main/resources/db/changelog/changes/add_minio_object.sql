CREATE TABLE `minio_object` (
                                `key_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'bucket/objectName',
                                `object_name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'objectName',
                                `size` int(11) NOT NULL COMMENT '对象大小',
                                `upload_time` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '对象上传日期'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;