

DROP TABLE IF EXISTS `como_file_configurations`;
SET character_set_client = utf8mb4 ;
CREATE TABLE `como_file_configurations` (
  `sr_no` int(11) NOT NULL AUTO_INCREMENT,
  `file_type` varchar(45) DEFAULT NULL,
  `sftp_url` varchar(45) DEFAULT NULL,
  `source_path` varchar(45) DEFAULT NULL,
  `sftp_port` int(11) DEFAULT NULL,
  `sftp_user` varchar(45) DEFAULT NULL,
  `dest_path` varchar(45) DEFAULT NULL,
  `sftp_password` varchar(45) DEFAULT NULL,
  `scheduled_date_time` datetime DEFAULT NULL,
  `description` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`sr_no`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
