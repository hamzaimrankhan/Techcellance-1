-- Scripts  18-08-2019
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