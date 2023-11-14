ALTER TABLE `cogent-admin`.`backpack`
DROP INDEX `sn_name_type_index`,
ADD UNIQUE INDEX `dev_name_unique`(`dev_name`) USING HASH,
ADD UNIQUE INDEX `sn_unique`(`sn`) USING HASH;