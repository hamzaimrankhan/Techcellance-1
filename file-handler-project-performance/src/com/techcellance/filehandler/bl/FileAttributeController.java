package com.techcellance.filehandler.bl;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techcellance.filehandler.beans.FileElementAttirbutes;
import com.techcellance.filehandler.dao.AbstractFileHandlerServiceDao;
import com.techcellance.filehandler.util.CommonUtils;

public class FileAttributeController {

	private static Logger LGR = LogManager.getLogger(FileAttributeController.class);
	
	private Map<String, FileElementAttirbutes> fileAttributes =null;
	
	public Map<String, FileElementAttirbutes> getFileAttributes() {
		return fileAttributes;
	}

	public FileAttributeController(int fileType) {
	
		fileAttributes =  new HashMap<String, FileElementAttirbutes>();
		populateFileAttributes(fileType);
	}
	
	private void populateFileAttributes(int fileType)
	{
		LGR.info(LGR.isInfoEnabled()? "Going to populate the file data configuration for file parsing":null);
		
		AbstractFileHandlerServiceDao serviceDao = AbstractFileHandlerServiceDao.getInstance();
		fileAttributes = serviceDao.poplateFileDataConfiguration(fileType);
		if(CommonUtils.isNullOrEmptyMap(fileAttributes)) {
			
			LGR.warn("##WARNING## File data configuration not defined");
		}
		
		LGR.info(LGR.isInfoEnabled()? "file data configuration being populated for file parsing":null);
	}
	
	
	public String getAttributeFromrecord(String record, String attribute) {
		
		FileElementAttirbutes fileAttirbute=  fileAttributes.get(attribute);
		int startPos = 0; 
		int endPos = 0; 
		
		if(CommonUtils.isNullObject(fileAttirbute)){
			return null;
		}		
		startPos = fileAttirbute.getStatrtPos() -1 ;
		endPos = fileAttirbute.getStatrtPos() + fileAttirbute.getLength()  - 1 ; 
		return record.substring(startPos,endPos).trim();
	}
	
	
	
}
