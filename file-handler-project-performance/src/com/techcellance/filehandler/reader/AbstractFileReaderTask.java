package com.techcellance.filehandler.reader;

import com.techcellance.filehandler.beans.FileConfiguration;
import com.techcellance.filehandler.beans.ResponseInfo;
import com.techcellance.filehandler.enums.FileFormat;
import com.techcellance.filehandler.reader.impl.BSPFileReaderTask;
import com.techcellance.filehandler.reader.impl.BinFileReaderTask;
import com.techcellance.filehandler.reader.impl.MidFileReaderTask;


public abstract class AbstractFileReaderTask 
{

	public static AbstractFileReaderTask getInstance(FileConfiguration fileConfiguration ,  String fileName) throws Exception
	{
		if(FileFormat.BSP == FileFormat.getFileFormatByFormatId(fileConfiguration.getFileType())){
			return new BSPFileReaderTask(fileConfiguration, fileName);
		}else if (FileFormat.BIN_FILE == FileFormat.getFileFormatByFormatId(fileConfiguration.getFileType())){
			return new BinFileReaderTask(fileConfiguration,fileName);
		}else if(FileFormat.MID_LOOKUP == FileFormat.getFileFormatByFormatId(fileConfiguration.getFileType())){
			 return new MidFileReaderTask(fileConfiguration, fileName);
		}
		throw new Exception("File format '" + fileConfiguration.getFileType() + "' not supported!");
	}
	public abstract ResponseInfo readAndValidateFile() throws Exception ;
	
	
}