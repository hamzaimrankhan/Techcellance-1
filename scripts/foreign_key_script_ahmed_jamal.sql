ALTER TABLE filehandler.como_file_configurations
DROP COLUMN  file_type;
ALTER TABLE filehandler.como_file_configurations 
ADD COLUMN file_type tinyint(1) NOT NULL AFTER `sr_no`;

SET SQL_SAFE_UPDATES = 0;

Update filehandler.como_file_configurations set file_type=1;
ALTER TABLE filehandler.como_file_configurations 
ADD CONSTRAINT FK_SFTP_TBLSYSAFILE_ID FOREIGN KEY (file_type) 
REFERENCES filehandler.tblsysfiles(id);

ALTER TABLE filehandler.file_data_configuration ADD file_type tinyint(1) NOT NULL DEFAULT 1;
ALTER TABLE filehandler.file_data_configuration 
ADD CONSTRAINT FK_ELEMENT_TBLSYSAFILE_ID FOREIGN KEY (file_type) 
REFERENCES filehandler.tblsysfiles(id);

ALTER TABLE filehandler.credit_entry_record
DROP COLUMN  file_type;
ALTER TABLE filehandler.credit_entry_record ADD file_type tinyint(1) NOT NULL DEFAULT 1;
ALTER TABLE filehandler.credit_entry_record
ADD CONSTRAINT FK_ENTRY_TBLSYSAFILE_ID FOREIGN KEY (file_type) 
REFERENCES filehandler.tblsysfiles(id);



