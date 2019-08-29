CREATE SCHEMA `filehandler` ;

CREATE TABLE `filehandler`.`como_file_configurations` (
  `sr_no` INT NOT NULL AUTO_INCREMENT,
  `file_type` VARCHAR(45) NULL,
  `sftp_url` VARCHAR(45) NULL,
  `source_path` VARCHAR(45) NULL,
  `sftp_port` INT NOT NULL,
  `sftp_user` VARCHAR(45) NULL,
  `dest_path` VARCHAR(45) NULL,
  `sftp_password` VARCHAR(45) NULL,
  `scheduled_date_time` DATETIME NULL,
  `description` VARCHAR(45) NULL,
  PRIMARY KEY (`sr_no`));
  
INSERT INTO `filehandler`.`como_file_configurations` (`file_type`, `sftp_url`, `source_path`, `sftp_port`, `sftp_user`, `dest_path`, `sftp_password`, `scheduled_date_time`, `description`) VALUES ('B', 'demo.wftpserver.com', '/input', '22', 'demo-user', '/output', 'password', curdate(), 'BSP File Handler');

CREATE TABLE `filehandler`.`settlement_files` (
  `file_sr_no` INT NOT NULL AUTO_INCREMENT,
  `file_name` VARCHAR(45) NULL,
  `total_successfull_record` INT NULL,
  `total_failed_records` INT NULL,
  `status` VARCHAR(45) NULL,
  PRIMARY KEY (`file_sr_no`),
  UNIQUE INDEX `file_sr_no_UNIQUE` (`file_sr_no` ASC) VISIBLE);
  
  CREATE TABLE `filehandler`.`credit_entry_record` (
  `sr_no` INT NOT NULL AUTO_INCREMENT,
  `invoice_number`VARCHAR(255) NULL,
  `agentCode` VARCHAR(255) NULL,
  `authorizationId` VARCHAR(255) NULL,
  `responseCode` VARCHAR(45) NULL,
  `responseDescription`VARCHAR(255) NULL,
  `maskedCardNumber` VARCHAR(255) NULL,
  `documentNumber` VARCHAR(255) NULL,
  `cardType` VARCHAR(255)NULL,
  `approvalCode` VARCHAR(255) NULL,
  `passenger` VARCHAR(255) NULL,
  `ammount` VARCHAR(255) NULL,
  `ticketCode` VARCHAR(255) NULL,
  `currency` VARCHAR(255) NULL,
  `arilineCode` VARCHAR(255) NULL,
  `passengerCode` VARCHAR(255) NULL,
  `departureTimeAirport` VARCHAR(255) NULL,
  `departureArrivalAirport` VARCHAR(255) NULL,
  `stopOverCode` VARCHAR(255) NULL,
  `IssuerCity` VARCHAR(255) NULL,
  `merchantAgreementId`VARCHAR(255) NULL,
  `InvoiceName`VARCHAR(255) NULL,
  `AirlineName`VARCHAR(255) NULL,
  `file_sr_no` DOUBLE NULL,
  PRIMARY KEY (`sr_no`),
  UNIQUE INDEX `sr_no_UNIQUE` (`sr_no` ASC) VISIBLE,
  INDEX `file_sr_no_idx` (`file_sr_no` ASC) VISIBLE
  );

  
ALTER TABLE `filehandler`.`credit_entry_record` 
ADD COLUMN `batch_no` VARCHAR(255) NULL AFTER `file_sr_no`,
ADD COLUMN `invoice_date` VARCHAR(255) NULL AFTER `batch_no`,
ADD COLUMN `mid` VARCHAR(255) NULL AFTER `invoice_date`;

ALTER TABLE `filehandler`.`credit_entry_record` 
ADD COLUMN `flight_code` VARCHAR(255) NULL AFTER `mid`;

CREATE TABLE `filehandler`.`payment_processor_configuration` (
  `srno` INT NOT NULL AUTO_INCREMENT,
  `configuration_name` VARCHAR(255) NULL,
  `url` VARCHAR(255) NOT NULL,
  `user` VARCHAR(255) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  PRIMARY KEY (`srno`));
INSERT INTO `filehandler`.`payment_processor_configuration` (`configuration_name`, `url`, `user`, `password`) VALUES ('world_pay', 'https://secure-test.worldpay.com/jsp/merchant/xml/paymentService.jsp', 'MH8RAH8918QRQCEL6P4U', 'COMOtest1!');

ALTER TABLE `filehandler`.`settlement_files` 
ADD COLUMN `recieved_at` DATETIME NULL AFTER `status`;

/*ALTER Scripts*/
ALTER TABLE `filehandler`.`credit_entry_record` 
ADD COLUMN `departure_date` VARCHAR(45) NULL AFTER `flight_code`,
ADD COLUMN `departure_month` VARCHAR(45) NULL AFTER `departure_date`,
ADD COLUMN `tax_amount` INT NULL AFTER `departure_month`,
CHANGE COLUMN `agentCode` `agent_code` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `authorizationId` `authorization_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `responseCode` `response_code` VARCHAR(45) NULL DEFAULT NULL ,
CHANGE COLUMN `responseDescription` `response_description` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `maskedCardNumber` `card_number` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `documentNumber` `document_number` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `cardType` `card_type` VARCHAR(45) NULL DEFAULT NULL ,
CHANGE COLUMN `approvalCode` `approval_code` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `ammount` `amount` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `ticketCode` `ticket_code` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `arilineCode` `ariline_code` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `passengerCode` `passenger_code` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `departureTimeAirport` `departure_airport` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `departureArrivalAirport` `arrival_airport` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `stopOverCode` `stop_over_code` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `IssuerCity` `issuer_city` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `merchantAgreementId` `merchant_agreement_id` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `InvoiceName` `invoice_name` VARCHAR(255) NULL DEFAULT NULL ,
CHANGE COLUMN `AirlineName` `airline_name` VARCHAR(255) NULL DEFAULT NULL ;







  
/*
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password';
  
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'password'

INSERT INTO mysql.user (Host, User, Password) VALUES ('%', 'root', password('1234'));
GRANT ALL ON *.* TO 'root'@'%' WITH GRANT OPTION;

SELECT user,authentication_string,plugin,host FROM mysql.user;
ALTER USER 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY '1234';
FLUSH PRIVILEGES;

*/



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

# After 22-06-2019 
#-------------------------------------------------------
#------ Start of 22-06-2019----------------------------- 

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

select mfi.acquirer_account From  mid_file_information mfi , bin_file_information bfi where bfi.brand = mfi.payment_method  and mfi.currency_code = ? and mfi.country_code = ? and mfi.company_id =? and bfi.range_from <= ? < bfi.range_until;

select 1 from como_file_configurations;
select * from como_file_configurations order by 1 desc ;
select * from como_file_params ;
select * from settlement_files order by 1 desc; 
select * from credit_entry_record where file_sr_no in (220);
select * from bin_file_information where file_sr_no in (191) order by 1 desc;
select * from mid_file_information where file_sr_no in (191) order by 1 desc;
select * from como_file_params;
select * from file_data_configuration ;

select * from mid_file_information ;
select * from bin_file_information;
select * from como_file_configurations ;
delete from mid_file_information ;
delete from bin_file_information;

select file_type, sftp_url, source_path, sftp_port, sftp_user, dest_path, sftp_password, scheduled_date_time,description,file_name_convention from como_file_configurations order by file_type desc 


;
-- Scripts for 8-8-2019
CREATE TABLE `filehandler`.`emails` (
  `sr_no` INT NOT NULL AUTO_INCREMENT,
  `email_template_id` VARCHAR(100) NOT NULL,
  `email_from` VARCHAR(256) NULL,
  `email_recipient` VARCHAR(256) NULL,
  `email_pass` VARCHAR(256) NULL,
  `email_host` VARCHAR(256) NULL,
  `email_port` INT NULL,
  `email_subject` VARCHAR(256) NULL,
  `email_body` VARCHAR(256) NULL,
  `email_footer` VARCHAR(256) NULL,
  `is_active` VARCHAR(1) NULL,
  PRIMARY KEY (`sr_no`),
  UNIQUE INDEX `email_template_id_UNIQUE` (`email_template_id` ASC) VISIBLE);

INSERT INTO `filehandler`.`emails` (`email_template_id`, `email_from`, `email_recipient`, `email_pass`, `email_host`, `email_port`, `email_subject`, `email_body`, `email_footer`, `is_active`) VALUES ('GEN_EMAIL_FAILED_RECORD', ' sentmail@gmpsgroup.com', 'your_email.com', 'bf#48UJg5TPX3Ruk', 'webmail.gmpsgroup.com', '587', 'COMO File Handler', 'Following ', 'Regards, \\n hamza imran khan ', 'Y');
INSERT INTO `filehandler`.`emails` (`email_template_id`, `email_from`, `email_recipient`, `email_pass`, `email_host`, `email_port`, `email_subject`, `email_body`, `email_footer`, `is_active`) VALUES ('GEN_EMAIL_SYSTEM_EXCEPTION', ' sentmail@gmpsgroup.com', 'your_email.com', 'bf#48UJg5TPX3Ruk', 'webmail.gmpsgroup.com', '587', 'System Exception', 'Following ', 'Regards, \\n hamza imran khan ', 'Y');

-- 21-08-2019 
ALTER TABLE `filehandler`.`mid_file_information` 
CHANGE COLUMN `currency_code` `currency_code` TEXT NULL DEFAULT NULL ;


