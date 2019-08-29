
CREATE TABLE `filehandler`.`file_data_configuration` (
  `sr_no` INT NOT NULL AUTO_INCREMENT,
  `element_name` VARCHAR(255) NULL,
  `identifier` VARCHAR(45) NULL,
  `start_pos` INT NOT NULL,
  `length` INT NOT NULL,
  `description` VARCHAR(255) NULL,
  PRIMARY KEY (`sr_no`));
  
ALTER TABLE `filehandler`.`credit_entry_record` 
ADD COLUMN `ticket_restricted` VARCHAR(45) NULL AFTER `tax_amount`;

ALTER TABLE `filehandler`.`credit_entry_record` 
ADD COLUMN `transaction_type` VARCHAR(45) NULL AFTER `ticket_restricted`,
ADD COLUMN `file_type` VARCHAR(45) NULL AFTER `transaction_type`;


  