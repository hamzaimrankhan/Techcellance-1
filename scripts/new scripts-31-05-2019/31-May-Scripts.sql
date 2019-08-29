insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("DepartureAirport","COR",36,5,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("DepartureAirport_2","COR",68,5,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("DepartureAirport_3","COR",100,5,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("DepartureAirport_4","COR",132,5,"");

insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("ArrivalAirport","COR",41,5,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("ArrivalAirport_2","COR",73,5,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("ArrivalAirport_3","COR",105,5,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("ArrivalAirport_4","COR",137,5,"");

insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("StopOverCode","COR",46,1,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("StopOverCode_2","COR",78,1,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("StopOverCode_3","COR",110,1,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("StopOverCode_4","COR",142,1,"");

insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("FlightCode","COR",47,2,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("FlightCode_2","COR",79,2,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("FlightCode_3","COR",111,2,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("FlightCode_4","COR",143,2,"");


insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("CarrierCode","COR",49,3,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("CarrierCode_2","COR",81,3,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("CarrierCode_3","COR",113,3,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("CarrierCode_4","COR",145,3,"");

insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("FareClass","COR",53,1,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("FareClass_2","COR",85,1,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("FareClass_3","COR",117,1,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("FareClass_4","COR",149,1,"");


insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("FareBasis","COR",54,14,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("FareBasis_2","COR",86,14,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("FareBasis_3","COR",118,14,"");
insert into filehandler.file_data_configuration(element_name,identifier,start_pos,length,description) values("FareBasis_4","COR",150,14,"");

ALTER TABLE `filehandler`.`credit_entry_record` 
CHANGE COLUMN `tax_amount` `tax_amount` DOUBLE NULL DEFAULT NULL ;
