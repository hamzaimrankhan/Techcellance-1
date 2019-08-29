
DROP TABLE IF EXISTS `payment_processor_configuration`;
 SET character_set_client = utf8mb4 ;
CREATE TABLE `payment_processor_configuration` (
  `srno` int(11) NOT NULL AUTO_INCREMENT,
  `configuration_name` varchar(255) DEFAULT NULL,
  `url` varchar(255) NOT NULL,
  `user` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  PRIMARY KEY (`srno`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
