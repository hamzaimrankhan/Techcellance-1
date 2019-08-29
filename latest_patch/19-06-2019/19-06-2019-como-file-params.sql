-------------------------
CREATE TABLE `filehandler`.`como_file_params` (
  `sr_no` INT NOT NULL AUTO_INCREMENT,
  `param_id` VARCHAR(45) NOT NULL,
  `param_value` VARCHAR(45) NOT NULL,
  `is_active` VARCHAR(1) NOT NULL,
  `description` VARCHAR(255) NULL,
  PRIMARY KEY (`sr_no`),
  UNIQUE INDEX `sr_no_UNIQUE` (`sr_no` ASC) VISIBLE);
  
INSERT INTO `filehandler`.`como_file_params` (`param_id`, `param_value`, `is_active`, `description`) VALUES ('CFH_THREAD_POOL_SIZE', '12', 'Y', 'no of CFH at a time ');
INSERT INTO `filehandler`.`como_file_params` (`param_id`, `param_value`, `is_active`, `description`) VALUES ('CBR_THREAD_POOL_SIZE', '30', 'Y', 'no of CBR at a time ');
