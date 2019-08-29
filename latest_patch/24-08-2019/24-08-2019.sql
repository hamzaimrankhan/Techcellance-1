-- 24-08-2019 
ALTER TABLE `filehandler`.`mid_file_information` 
CHANGE COLUMN `currency_code` `currency_code` TEXT NULL DEFAULT NULL ;

INSERT INTO `filehandler`.`como_file_params` (`param_id`, `param_value`, `is_active`, `description`) VALUES ('MID_FILE_NO_OF_DAYS', '1', 'Y', 'Number of days to be subtracted');
