

DROP TABLE IF EXISTS `settlement_files`;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `settlement_files` (
  `file_sr_no` int(11) NOT NULL AUTO_INCREMENT,
  `file_name` varchar(45) DEFAULT NULL,
  `total_successfull_record` int(11) DEFAULT NULL,
  `total_failed_records` int(11) DEFAULT NULL,
  `status` varchar(45) DEFAULT NULL,
  `recieved_at` datetime DEFAULT NULL,
  PRIMARY KEY (`file_sr_no`),
  UNIQUE KEY `file_sr_no_UNIQUE` (`file_sr_no`)
) ENGINE=InnoDB AUTO_INCREMENT=106 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
