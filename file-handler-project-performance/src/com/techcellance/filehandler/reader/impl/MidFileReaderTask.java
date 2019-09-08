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
import com.techcellance.filehandler.beans.FailedEntryInfo;
import com.techcellance.filehandler.beans.FileConfiguration;
import com.techcellance.filehandler.beans.MIDFile;
import com.techcellance.filehandler.beans.MerchantInformation;
import com.techcellance.filehandler.beans.ResponseInfo;
import com.techcellance.filehandler.enums.ResponseCode;
import com.techcellance.filehandler.reader.AbstractFileReaderTask;
import com.techcellance.filehandler.util.CommonUtils;
import com.techcellance.filehandler.util.Constants;
import com.techcellance.filehandler.util.FTPUtil;

public class MidFileReaderTask extends AbstractFileReaderTask {

	private static Logger LGR = LogManager.getLogger(BinFileReaderTask.class);
	private String fileName  = null ;
	private FileConfiguration  fileConfiguration = null ;

	public MidFileReaderTask( FileConfiguration fileConfiguration , String fileName){
		this.fileName = fileName; 
		this.fileConfiguration = fileConfiguration;
	}
	
	@Override
	public ResponseInfo readAndValidateFile() throws Exception{
	
		ResponseInfo responseInfo = new ResponseInfo();
		BufferedReader br = null;
		ChannelSftp channelSftp= null;
		Session session= null ;
		MIDFile midFile = null ;
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
			midFile= readfileAndValidateData(br);	
		}
		
		if(!CommonUtils.isNullObject(midFile)) {
		CommonUtils.populateResponseInfo(responseInfo, ResponseCode.SUCCESS.getRespCode(), ResponseCode.SUCCESS.getRespDesc(), midFile);
		
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
	
	
	
	private MIDFile readfileAndValidateData(BufferedReader br) throws Exception {
		String  line = null;
		MIDFile midFile = new MIDFile();
		MerchantInformation merchantInformation = null; 
		int recNumber = 0 ;
			
		line = readLine(br);
		if(null == line ){
			LGR.debug("File is empty or null with file name  : " + fileName);
			return null;
		}	
		
		while(null != line  && !line.startsWith(Constants.MID_FILE_HEADER_IDENTIFER)){
			line = readLine(br);
		}
		
		line = readLine(br);
		
		while(null != line){
		
			LGR.debug(LGR.isDebugEnabled()? "Line read from file is : " + line:null);
			merchantInformation = populateMIDAndValidateFileRecord(line);
			merchantInformation.setRecNumber(++recNumber);
			if(validateMerchantFileInformation(midFile,merchantInformation)) {
				midFile.getMerchantInformationList().add(merchantInformation);
			}
			line = readLine(br);
			
		}	
			
		return midFile;
	}

	private boolean  validateMerchantFileInformation(MIDFile midFile, MerchantInformation merchantInformation) {
		
		FailedEntryInfo fInfo =  new FailedEntryInfo(); ;
		boolean isValidRecord = true;
		
		if(CommonUtils.isNullOrEmptyString(merchantInformation.getMerchantCode())) {
			fInfo.getMissingColumns().add("MerchantCode");
		}
		if(CommonUtils.isNullOrEmptyString(merchantInformation.getAcquirerAccount())){
			fInfo.getMissingColumns().add("AcquirerAccount");
		}
		if (CommonUtils.isNullOrEmptyString(merchantInformation.getiATANumericalCarrierCode())) {
			fInfo.getMissingColumns().add("IATANumericalCarrierCode");
		}
		if (CommonUtils.isNullOrEmptyString(merchantInformation.getAccAccountCountryCode())) {
			fInfo.getMissingColumns().add("AccAccountCountryCode");
		}
		if (CommonUtils.isNullOrEmptyString(merchantInformation.getCurrencyCode())) {
			fInfo.getMissingColumns().add("CurrencyCode");
		}
		if (CommonUtils.isNullOrEmptyString(merchantInformation.getPaymentMethod())) {
			fInfo.getMissingColumns().add("PaymentMethod");
		}
		if (CommonUtils.isNullOrEmptyString(merchantInformation.getLegalEntityAddress1())) {
			fInfo.getMissingColumns().add("LegalEntityAddress1");
		}
		if (CommonUtils.isNullOrEmptyString(merchantInformation.getLegalEntityCity())) {
			fInfo.getMissingColumns().add("LegalEntityCity");
		}
		if (CommonUtils.isNullOrEmptyString(merchantInformation.getLegalentityPostCode())) {
			fInfo.getMissingColumns().add("LegalentityPostCode");
		}
		if (CommonUtils.isNullOrEmptyString(merchantInformation.getCountryCode())) {
			fInfo.getMissingColumns().add("CountryCode");
		}
		if (!CommonUtils.isNullOrEmptyCollection(fInfo.getMissingColumns())) {
			isValidRecord  = false;
			midFile.getFailedEntryInfos().add(fInfo);
			fInfo.setEntryRecNo(merchantInformation.getRecNumber());
		}
	
		return isValidRecord;
	}

	private MerchantInformation populateMIDAndValidateFileRecord( String line) throws Exception{
		int index = 0 ;
		List<String> midFileDataString = convertStringToCommaSeperatedList(line);
		MerchantInformation merchantInformation  = new MerchantInformation(); 
		merchantInformation.setCaptureOnlyAgent(midFileDataString.get(index++));
		merchantInformation.setMerchantCode(midFileDataString.get(index++));
		merchantInformation.setAcquirerAccount(midFileDataString.get(index++));
		merchantInformation.setiATANumericalCarrierCode(midFileDataString.get(index++));;
		merchantInformation.setAccAccountCountryCode(midFileDataString.get(index++));
		merchantInformation.setCurrencyCode(midFileDataString.get(index++));
		merchantInformation.setPaymentMethod(midFileDataString.get(index++));
		merchantInformation.setCompanyID(midFileDataString.get(index++));
		merchantInformation.setLegalEntityAddress1(midFileDataString.get(index++));
		merchantInformation.setLegalEntityCity(midFileDataString.get(index++));
		merchantInformation.setLegalentityPostCode(midFileDataString.get(index++));
		merchantInformation.setCountryCode(midFileDataString.get(index++));
		
		return merchantInformation ;
	}

	private List<String> convertStringToCommaSeperatedList(String line) throws Exception{		
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
			if((temp == null) || (temp.trim().length() > 1)){
				break;
			}
		}	   
		return temp;
	}

	
	
}
