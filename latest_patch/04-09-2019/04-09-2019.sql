-- 04-09-2019
ALTER TABLE `filehandler`.`settlement_files` 
CHANGE COLUMN `file_name` `file_name` VARCHAR(255) NULL DEFAULT NULL ;
INSERT INTO `filehandler`.`como_file_params` (`param_id`, `param_value`, `is_active`, `description`) VALUES ('BIN_FILE_NO_OF_DAYS', '1', 'Y', 'Number of days to be subtracted');
