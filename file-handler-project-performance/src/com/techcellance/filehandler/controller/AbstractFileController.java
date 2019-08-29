package com.techcellance.filehandler.controller;

import com.techcellance.filehandler.beans.FileConfiguration;
import com.techcellance.filehandler.beans.ResponseInfo;
import com.techcellance.filehandler.controller.impl.BSPFileController;
import com.techcellance.filehandler.controller.impl.BinFileController;
import com.techcellance.filehandler.controller.impl.MidFileController;
import com.techcellance.filehandler.enums.FileFormat;

public abstract class AbstractFileController
{

	public static AbstractFileController getInstance(FileConfiguration fileConfiguration ) throws Exception
	{
		if(FileFormat.BSP == FileFormat.getFileFormatByFormatId(fileConfiguration.getFileType())){
			return new BSPFileController(fileConfiguration);
		
		}else if (FileFormat.BIN_FILE == FileFormat.getFileFormatByFormatId(fileConfiguration.getFileType())){
		
			return new BinFileController(fileConfiguration);
			
		}else if(FileFormat.MID_LOOKUP == FileFormat.getFileFormatByFormatId(fileConfiguration.getFileType())){
			
			return new MidFileController(fileConfiguration);
		}
		throw new Exception("File format '" + fileConfiguration.getFileType() + "' not supported!");
	}
	public abstract ResponseInfo processFiles();
	
}