DROP TABLE IF EXISTS `file_data_configuration`;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `file_data_configuration` (
  `sr_no` int(11) NOT NULL AUTO_INCREMENT,
  `element_name` varchar(255) DEFAULT NULL,
  `identifier` varchar(45) DEFAULT NULL,
  `start_pos` int(11) NOT NULL,
  `length` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`sr_no`)
) ENGINE=InnoDB AUTO_INCREMENT=84 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
