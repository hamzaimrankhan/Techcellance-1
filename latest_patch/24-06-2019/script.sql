
ALTER TABLE `filehandler`.`como_file_configurations` 
ADD COLUMN `file_name_convention` VARCHAR(45) NULL;
ALTER TABLE `filehandler`.`settlement_files` 
ADD INDEX `file_name_status_index` USING BTREE (`file_name`, `status`) VISIBLE;
;