package com.techcellance.filehandler.reader.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.techcellance.filehandler.beans.BinFile;
import com.techcellance.filehandler.beans.BinInformation;
import com.techcellance.filehandler.beans.FailedEntryInfo;
import com.techcellance.filehandler.beans.FileConfiguration;
import com.techcellance.filehandler.beans.ResponseInfo;
import com.techcellance.filehandler.enums.ResponseCode;
import com.techcellance.filehandler.reader.AbstractFileReaderTask;
import com.techcellance.filehandler.util.CommonUtils;
import com.techcellance.filehandler.util.Constants;
import com.techcellance.filehandler.util.FTPUtil;

public  class BinFileReaderTask extends AbstractFileReaderTask{
	private static Logger LGR = LogManager.getLogger(BinFileReaderTask.class);
	private String fileName  = null ;
	private FileConfiguration  fileConfiguration = null ;

	public BinFileReaderTask( FileConfiguration fileConfiguration , String fileName)
	{
		this.fileName = fileName; 
		this.fileConfiguration = fileConfiguration;
	}
	

	@Override
	public ResponseInfo readAndValidateFile() throws Exception{
	
		ResponseInfo responseInfo = new ResponseInfo();
		BufferedReader br = null;
		ChannelSftp channelSftp= null;
		Session session= null ;
		BinFile binFile = null ;
		try {
			
		session = FTPUtil.getSession(fileConfiguration);
		if(null == session) {
				LGR.info(LGR.isInfoEnabled()? " Could  not get the session  so unable to fetch the list of files" : null );
				CommonUtils.populateResponseInfo(responseInfo,ResponseCode.COULD_NOT_MAKE_CONNECTION.getRespCode(),ResponseCode.COULD_NOT_MAKE_CONNECTION.getRespDesc(), null);
				return responseInfo;			
		}
		channelSftp = FTPUtil.getSFTPChannel(session);
		
		br = FTPUtil.readFileFromSftpServer(fileConfiguration, fileName,channelSftp);		
	
		//Temporary Code 
		if(!CommonUtils.isNullOrEmptyString(Constants.TEMP_FILE_PATH )){
		
			LGR.info(LGR.isInfoEnabled()? "INFO: This feature is temporary ON for testing puprpose, so going to read file from path : " + Constants.TEMP_FILE_PATH:null);
			FileReader fr = null;
			fr = new FileReader(Constants.TEMP_FILE_PATH);
			br = new BufferedReader(fr);
			
		}
		
		
		if(!CommonUtils.isNullObject(br)){
			LGR.info(LGR.isInfoEnabled() ? "Going to read and parse file":null );
			binFile = readfileAndValidateData(br);	
		}
		
		if(!CommonUtils.isNullObject(binFile)) {
		CommonUtils.populateResponseInfo(responseInfo, ResponseCode.SUCCESS.getRespCode(), ResponseCode.SUCCESS.getRespDesc(), binFile);
		
		}
		else {
		CommonUtils.populateResponseInfo(responseInfo, ResponseCode. FILE_READING_NOT_SUCCESSFUL.getRespCode(), ResponseCode.FILE_READING_NOT_SUCCESSFUL.getRespDesc(), null);	
		}
			
		
		}finally
		{
			CommonUtils.closeBufferReader(br);
				FTPUtil.closeSFTPChannel(channelSftp);
				FTPUtil.closeSession(session);
	    
		}
		return responseInfo;
	}
	
	
	
	private BinFile readfileAndValidateData(BufferedReader br) throws Exception {
		String  line = null;
		BinFile BinFile = new BinFile();
		int recNumber  = 0;
		BinInformation  binInformation= null;
			
		line = readLine(br);
		if(null == line ){
			LGR.debug("File is empty or null with file name  : " + fileName);
			return null;
		}	
		
		while(null != line  && !line.startsWith(Constants.BIN_FILE_HEADER_IDENTIFIER)){
			line = readLine(br);
		}
		
		line = readLine(br);
		
		while(null != line){
		
			LGR.debug(LGR.isDebugEnabled()? "Line read from file is : " + line:null);
			binInformation = populateAndValidateBinFileRecord(line);
			binInformation.setRecNumber(++recNumber);
			
			if(validateBinFileInformation(BinFile , binInformation)) {
				BinFile.getBinInformationList().add(binInformation);
			}
			line = readLine(br);
			
		}	
			
		return BinFile;
	
	}

	private boolean validateBinFileInformation(BinFile binFile, BinInformation binInformation) {
		boolean isValidRecord = true; 
		
		if(CommonUtils.isNullOrEmptyString(binInformation.getBrand())){
			isValidRecord = false ;
			FailedEntryInfo fInfo = new FailedEntryInfo() ;
			fInfo.setEntryRecNo(binInformation.getRecNumber());
			fInfo.getMissingColumns().add("Brand");
			binFile.getFailedEntryInfos().add(fInfo);
		}
		return isValidRecord;
	}
	


	private BinInformation populateAndValidateBinFileRecord( String line) throws Exception {
		
		List<String> binFileString = convertStringToCommaSeperatedList(line);
		BinInformation binInfo  = new BinInformation(); 
		binInfo.setRangeFrom(Long.parseLong(binFileString.get(0)));
		binInfo.setRangeUntil(Long.parseLong(binFileString.get(1)));
		binInfo.setCountry(binFileString.get(2));
		binInfo.setBrand(binFileString.get(3));
		binInfo.setIssuer(binFileString.get(4));
		binInfo.setFamily(binFileString.get(5));
		return binInfo;
		
	}


	private List<String> convertStringToCommaSeperatedList(String line)throws Exception {
		
		String[] commaSeparatedArr = line.split("\\s*,\\s*",-1);
	    List<String> result = Arrays.stream(commaSeparatedArr).collect(Collectors.toList());
		return result;
	}


	public String readLine(BufferedReader bf) throws IOException
	{
		String temp = "";
		   
		while(true)
		{
			temp = bf.readLine();
			
			if((temp == null) || (temp.trim().length() > 1))
			{
				break;
			}
		}
		   
		return temp;
	}

}