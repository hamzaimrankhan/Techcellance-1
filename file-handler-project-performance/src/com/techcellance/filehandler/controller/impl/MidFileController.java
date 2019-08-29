package com.techcellance.filehandler.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techcellance.filehandler.Email.EmailGenerationHandler;
import com.techcellance.filehandler.beans.FileConfiguration;
import com.techcellance.filehandler.beans.MIDFile;
import com.techcellance.filehandler.beans.MerchantInformation;
import com.techcellance.filehandler.beans.ResponseInfo;
import com.techcellance.filehandler.controller.AbstractFileController;
import com.techcellance.filehandler.dao.AbstractFileHandlerServiceDao;
import com.techcellance.filehandler.enums.ResponseCode;
import com.techcellance.filehandler.reader.AbstractFileReaderTask;
import com.techcellance.filehandler.threadpool.MidEntryThreadPool;
import com.techcellance.filehandler.util.CommonUtils;
import com.techcellance.filehandler.util.Constants;
import com.techcellance.filehandler.util.FTPUtil;

public class MidFileController extends AbstractFileController {

	
	private static Logger LGR = LogManager.getLogger(MidFileController.class);
	private FileConfiguration  fileConfiguration = null; 
	AtomicInteger successFulRecordCount = new AtomicInteger(0);
	AtomicInteger  failedRecordCount = new AtomicInteger(0) ;
	
	public MidFileController(FileConfiguration fileConfiguration) {
		this.fileConfiguration = fileConfiguration ; 
		this.fileConfiguration.setnDaysBeforeFile(Constants.MID_FILE_NO_OF_DAYS);
	}

	@Override
	public ResponseInfo processFiles()
	{
		ResponseInfo responseInfo = new ResponseInfo();
		List<String> files = null;
		try {
			
		
		LGR.info("Going to pick the all files from the sftp source destination");
		files = FTPUtil.getListOfFilesFromSftpServer(fileConfiguration);
		if(!CommonUtils.isNullOrEmptyCollection(files)) {	
		CommonUtils.removeAlreadyPocessedFilesFromTheList(files);
		
		}
		
		if(CommonUtils.isNullOrEmptyCollection(files)){
			
			LGR.info(LGR.isInfoEnabled() ? "No files found on following location "  + fileConfiguration.getSoucrePath(): null);
			CommonUtils.populateResponseInfo(responseInfo, ResponseCode.NO_FILE_FOUND_SFTP_FOLDER.getRespCode(), ResponseCode.NO_FILE_FOUND_SFTP_FOLDER.getRespDesc(), null);
			return responseInfo;
		}		
		
		LGR.info(LGR.isInfoEnabled()?"Going to truncate mid_file_information data  from the database":null);
		AbstractFileHandlerServiceDao.getInstance().truncateMidInformation();
		LGR.info(LGR.isInfoEnabled()? "End of truncation of mid_file_informaton from the database": null);
		
		for (String fileName : files) {	
			LGR.info(LGR.isInfoEnabled()? "Going to process the MID File with file type : " + fileConfiguration.getFileType() + " and file name = " + fileName: null);
			responseInfo = proccessFile(fileName);
			LGR.info(LGR.isInfoEnabled()? " Response recieved from MID file ( " + fileName + ") is " + responseInfo.getRespCode()  + " , response description: " + responseInfo.getRespDesc()  : null); 
		}
		
		}catch(Exception e)
		{
			LGR.error("Exception occurred in MIDFileController.call  Exception : " + e.getMessage(), e);
			CommonUtils.populateResponseInfo(responseInfo, ResponseCode.SYSTEM_MALFUNCTION.getRespCode(), e.getMessage(), e);
			CompletableFuture.runAsync(() -> EmailGenerationHandler.generateEmailForSystemException(Constants.GEN_EMAIL_SYSTEM_EXCEPTION,e));
			return responseInfo;
		}
		finally
		{
			
		}
		
		CommonUtils.populateResponseInfo(responseInfo, ResponseCode.SUCCESS.getRespCode(), ResponseCode.SUCCESS.getRespDesc(), null);
		return responseInfo;

	}

	
	private ResponseInfo proccessFile(String fileName) throws Exception{
		
		ResponseInfo responseInfo = null;
		boolean isErrorRecordExists =false ;	
			LGR.info(LGR.isInfoEnabled()?"In MID File Controller Task.processFile"  :null );
			
			LGR.info(LGR.isInfoEnabled() ?"Going to read the file("+ fileName +") from the SFTP server to persist bin information  with starting time: " + CommonUtils.getCurrentFormatTime(Constants.DATE_TIME_FORMAT) : null );
			AbstractFileReaderTask fileReaderHandler =  AbstractFileReaderTask.getInstance(fileConfiguration, fileName);	
			responseInfo = fileReaderHandler.readAndValidateFile();
			LGR.info(LGR.isInfoEnabled()?" Response recieved from readAndParseFile : " + responseInfo.getRespCode()  + " response descritpion : " + responseInfo.getRespDesc() :null);
			
			if(!CommonUtils.isNullObject(responseInfo) && responseInfo.getRespCode().equalsIgnoreCase(ResponseCode.SUCCESS.getRespCode()))
			{
				MIDFile midFile  = (MIDFile)(responseInfo.getReturnData());
				midFile.setFileName(fileName);
				isErrorRecordExists = isErrorRecordsExits(midFile);
					responseInfo = processMIDFileExecution(midFile,fileName);

			if (isErrorRecordExists) {
				CompletableFuture.runAsync(() -> EmailGenerationHandler.generateEmailForFailedRecords(
						Constants.GEN_EMAIL_FAILED_RECORD, midFile.getFailedEntryInfos(), midFile.getFileName()));
				
			}
			
			}	
			LGR.info(LGR.isInfoEnabled() ?"End of reading the file("+ fileName +") from the SFTP server  with ending time: " + CommonUtils.getCurrentFormatTime(Constants.DATE_TIME_FORMAT) : null );
			return responseInfo;
		
	}

    private ResponseInfo processMIDFileExecution(MIDFile midFile, String fileName) throws Exception {
    
		boolean updateFilestatus = false;
		Long fileSrNo = null;
		AbstractFileHandlerServiceDao dao = null;
		ResponseInfo responseInfo = new ResponseInfo();

		dao = AbstractFileHandlerServiceDao.getInstance();

		fileSrNo = dao.persistFile(fileName);

		boolean status = processMidFiles(midFile, fileSrNo);

		updateFilestatus = dao.updateFile(fileName, fileSrNo, this.successFulRecordCount, this.failedRecordCount,
				status ? Constants.SUCCESSFUL_STATUS : Constants.FAIL);

		if (updateFilestatus) {
			CommonUtils.populateResponseInfo(responseInfo, ResponseCode.SUCCESS.getRespCode(),
					ResponseCode.SUCCESS.getRespDesc(), null);
		} else {
			CommonUtils.populateResponseInfo(responseInfo, ResponseCode.FAIL.getRespCode(),
					ResponseCode.FAIL.getRespDesc(), null);
		}

		return responseInfo;
    }

	private boolean isErrorRecordsExits(MIDFile midFile) throws Exception{
		
		return (midFile.getFailedEntryInfos().size() > 0 );
	}

	@SuppressWarnings("rawtypes")
	private boolean processMidFiles(MIDFile midFile, Long fileSrNo)throws Exception {
    		  
        try {

        	List<CompletableFuture> processBinEntryFutureList = new ArrayList<>();
            this.successFulRecordCount.set(midFile.getMerchantInformationList().size());
            List<List<MerchantInformation>> subMerchantInfoList =  ListUtils.partition(midFile.getMerchantInformationList(), Constants.BIN_RECORD_BATCH_SIZE);
            int threadPool = subMerchantInfoList.size()/2;
            
            MidEntryThreadPool.initializePool(threadPool!= 0 ? threadPool :1);
        	
            
            for (List<MerchantInformation> records:subMerchantInfoList) {
                processBinEntryFutureList.add(CompletableFuture.runAsync(() -> persistMerchantInformation(records, fileSrNo), MidEntryThreadPool.getThreadPool()));
            }
        
           CompletableFuture.allOf(processBinEntryFutureList.toArray(new CompletableFuture[processBinEntryFutureList.size()])).get();        
           return true;
           
        } catch (Exception e) {
            LGR.error("##Exception## while processing bin entries: ", e);
            return false;
        } finally {
        	MidEntryThreadPool.shutDownPool();
        }
   
    }

    private boolean persistMerchantInformation(List<MerchantInformation> merchantInformations,Long fileSrNo) {
	
			AbstractFileHandlerServiceDao dao = AbstractFileHandlerServiceDao.getInstance();
			return dao.persistMerchantFileInformation(merchantInformations, fileSrNo);
    }
	

}
