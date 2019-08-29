ALTER TABLE `filehandler`.`como_file_configurations` 
ADD COLUMN `file_name_convention` VARCHAR(45) NULL;
ALTER TABLE `filehandler`.`settlement_files` 
ADD INDEX `file_name_status_index` USING BTREE (`file_name`, `status`) VISIBLE;

CREATE TABLE `filehandler`.`bin_file_information` (
  `sr_no` INT NOT NULL AUTO_INCREMENT,
  `file_sr_no` DOUBLE NULL,
  `range_from` LONG NULL,
  `range_until` LONG  NULL,
  `country` VARCHAR(100) NULL,
  `brand` VARCHAR(100) NULL,
  `issuer` VARCHAR(100) NULL,
  `family` VARCHAR(100) NULL,
  PRIMARY KEY (`sr_no`));

INSERT INTO `filehandler`.`tblsysfiles` (`id`, `name`) VALUES ('2', 'BIN');
INSERT INTO `filehandler`.`tblsysfiles` (`id`, `name`) VALUES ('3', 'MID');
INSERT INTO `filehandler`.`como_file_configurations` (`file_type`, `sftp_url`, `source_path`, `sftp_port`, `sftp_user`, `dest_path`, `sftp_password`, `scheduled_date_time`, `description`, `end_schedule`, `start_schedule`, `file_name_convention`) VALUES ('2', '195.35.90.22', 'test_source/bin/', '22', 'SFG_WPG_COMO', 'test_processed/', 'E:/id_rsa.ppk', '2019-03-31 00:00:00', 'BSP File Handler', '23:01:00', '00:01:00', 'COMO.*.yyyyMMdd.csv');
INSERT INTO `filehandler`.`como_file_configurations` (`file_type`, `sftp_url`, `source_path`, `sftp_port`, `sftp_user`, `dest_path`, `sftp_password`, `scheduled_date_time`, `description`, `end_schedule`, `start_schedule`, `file_name_convention`) VALUES ('3', '195.35.90.22', 'test_source/mid/', '22', 'SFG_WPG_COMO', 'test_processed/', 'E:/id_rsa.ppk', '2019-03-31 00:00:00', 'BSP File Handler', '23:01:00', '00:01:00', 'COMO.*.yyyyMMdd.csv');
INSERT INTO `filehandler`.`como_file_params` (`param_id`, `param_value`, `is_active`, `description`) VALUES ('BIN_RECORD_BATCH_SIZE', '1000', 'Y', 'Bin entrry record batch size ');
CREATE TABLE `filehandler`.`mid_file_information` (
  `sr_no` INT NOT NULL AUTO_INCREMENT,
  `file_sr_no` DOUBLE NULL,
  `capture_only_agent` VARCHAR(100) NULL,
  `merchant_code` VARCHAR(100) NULL,
  `acquirer_account` VARCHAR(100) NULL,
  `iata_carrier_code` VARCHAR(100) NULL,
  `aa_country_code` VARCHAR(100) NULL,
  `currency_code` VARCHAR(100) NULL,
  `payment_method` VARCHAR(100) NULL,
  `company_id` VARCHAR(100) NULL,
  `legal_entity_address` VARCHAR(100) NULL,
  `legal_entity_city` VARCHAR(100) NULL,
  `legal_post_code` VARCHAR(100) NULL,
  `country_code` VARCHAR(100) NULL,
  PRIMARY KEY (`sr_no`));
ALTER TABLE `filehandler`.`credit_entry_record` 
CHANGE COLUMN `invoice_date` `invoice_date` DATETIME NULL DEFAULT NULL ;
INSERT INTO `filehandler`.`file_data_configuration` (`element_name`, `identifier`, `start_pos`, `length`, `file_type`) VALUES ('CountryCode', 'CFH', '85', '2', '1');
