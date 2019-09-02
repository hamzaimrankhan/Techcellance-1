package com.techcellance.filehandler.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.collections4.ListUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techcellance.filehandler.Email.EmailGenerationHandler;
import com.techcellance.filehandler.beans.BinFile;
import com.techcellance.filehandler.beans.BinInformation;
import com.techcellance.filehandler.beans.FileConfiguration;
import com.techcellance.filehandler.beans.ResponseInfo;
import com.techcellance.filehandler.controller.AbstractFileController;
import com.techcellance.filehandler.dao.AbstractFileHandlerServiceDao;
import com.techcellance.filehandler.enums.ResponseCode;
import com.techcellance.filehandler.reader.AbstractFileReaderTask;
import com.techcellance.filehandler.threadpool.ThreadPool;
import com.techcellance.filehandler.util.CommonUtils;
import com.techcellance.filehandler.util.Constants;
import com.techcellance.filehandler.util.FTPUtil;

public class BinFileController extends AbstractFileController {

	
	private static Logger LGR = LogManager.getLogger(BinFileController.class);
	private FileConfiguration  fileConfiguration = null; 
	AtomicInteger successFulRecordCount = new AtomicInteger(0);
	AtomicInteger  failedRecordCount = new AtomicInteger(0) ;
	
	public BinFileController(FileConfiguration fileConfiguration) {
		this.fileConfiguration = fileConfiguration ; 
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
		
		LGR.info(LGR.isInfoEnabled()?"Going to truncate bin_file_information data  from the database":null);
		AbstractFileHandlerServiceDao.getInstance().truncateBinInformation();
		LGR.info(LGR.isInfoEnabled()? "End of truncation of bin_file_informaton from the database": null);
		
		for (String fileName : files) {
			
			LGR.info(LGR.isInfoEnabled()? "Going to process the Bin File with file type : " + fileConfiguration.getFileType() + " and file name = " + fileName: null);
			responseInfo = proccessFile(fileName);
			LGR.info(LGR.isInfoEnabled()? " Response recieved from Bin file ( " + fileName + ") is " + responseInfo.getRespCode()  + " , response description: " + responseInfo.getRespDesc()  : null); 
		}
		}catch(Exception e)
		{
			LGR.error("Exception occurred in BinFileController.call  Exception : " + e.getMessage(), e);
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

	private ResponseInfo proccessFile(String fileName) throws Exception {
		
		
		ResponseInfo responseInfo = null;
		
		boolean isErrorRecordExist = false;
	
			LGR.info(LGR.isInfoEnabled()?"In BIN File Controller Task.processFile"  :null );
			
			LGR.info(LGR.isInfoEnabled() ?"Going to read the file("+ fileName +") from the SFTP server to persist bin information  with starting time: " + CommonUtils.getCurrentFormatTime(Constants.DATE_TIME_FORMAT) : null );
			AbstractFileReaderTask fileReaderHandler =  AbstractFileReaderTask.getInstance(fileConfiguration, fileName);	
			responseInfo = fileReaderHandler.readAndValidateFile();
			LGR.info(LGR.isInfoEnabled()?" Response recieved from readAndParseFile : " + responseInfo.getRespCode()  + " response descritpion : " + responseInfo.getRespDesc() :null);
			
			if(!CommonUtils.isNullObject(responseInfo) && responseInfo.getRespCode().equalsIgnoreCase(ResponseCode.SUCCESS.getRespCode()))
			{
				BinFile binFile  = (BinFile)(responseInfo.getReturnData());
				binFile.setFileName(fileName);
				isErrorRecordExist = isErrorRecordExists(binFile);
				
					responseInfo = processBinFileExecution(binFile,fileName);
					
			if (isErrorRecordExist) {
				CompletableFuture.runAsync(() -> EmailGenerationHandler.generateEmailForFailedRecords(
						Constants.GEN_EMAIL_FAILED_RECORD, binFile.getFailedEntryInfos(), binFile.getFileName()));
			}
				
			}			
			
			LGR.info(LGR.isInfoEnabled() ?"End of reading the file("+ fileName +") from the SFTP server  with ending time: " + CommonUtils.getCurrentFormatTime(Constants.DATE_TIME_FORMAT) : null );
			return responseInfo;
		
		
	}

    private ResponseInfo processBinFileExecution(BinFile binFile, String fileName) throws Exception {
    	
    	boolean updateFilestatus = false;
		Long fileSrNo = null ;
		AbstractFileHandlerServiceDao dao = null ;
		ResponseInfo responseInfo = new ResponseInfo() ;
    	dao = AbstractFileHandlerServiceDao.getInstance();
		fileSrNo = dao.persistFile(fileName);
		boolean status  = processBinFiles( binFile,fileSrNo);
		updateFilestatus =   dao.updateFile(fileName,fileSrNo,this.successFulRecordCount,this.failedRecordCount,status?Constants.SUCCESSFUL_STATUS:Constants.FAIL);
		
		
		if (updateFilestatus){	
			CommonUtils.populateResponseInfo(responseInfo, ResponseCode.SUCCESS.getRespCode(), ResponseCode.SUCCESS.getRespDesc(), null);
		}else{
			CommonUtils.populateResponseInfo(responseInfo, ResponseCode.FAIL.getRespCode(), ResponseCode.FAIL.getRespDesc(), null);
		}
		
		return responseInfo;
	}

	private boolean isErrorRecordExists(BinFile binFile) throws Exception{
		
		return binFile.getTotalFaioeldRecord() > 0;
	}

	@SuppressWarnings("rawtypes")
	private boolean processBinFiles(BinFile binFile, Long fileSrNo)throws Exception {        
       
    	List<CompletableFuture> processBinEntryFutureList = new ArrayList<>();
    	ThreadPool threadPool = new ThreadPool();
       try {


            this.successFulRecordCount.set(binFile.getBinInformationList().size());
            List<List<BinInformation>> subBinInfoList =  ListUtils.partition(binFile.getBinInformationList(), Constants.BIN_RECORD_BATCH_SIZE);
            int threadPoolSize = subBinInfoList.size()/2 ; 
            threadPool.initializePool(threadPoolSize> 0 ? threadPoolSize : 1 );
        	
            
            for (List<BinInformation> records:subBinInfoList) {
                processBinEntryFutureList.add(CompletableFuture.runAsync(() -> persistBinInformation(records, fileSrNo), threadPool.getThreadPool()));
            }
        
           CompletableFuture.allOf(processBinEntryFutureList.toArray(new CompletableFuture[processBinEntryFutureList.size()])).get();        
           return true;
           
        } catch (Exception e) {
            LGR.error("##Exception## while processing bin entries: ", e);
            return false;
        } finally {
        	threadPool.shutDownPool();
        }
    }

    private boolean persistBinInformation(List<BinInformation> binFileInfos,Long fileSrNo) {
        	AbstractFileHandlerServiceDao dao = AbstractFileHandlerServiceDao.getInstance();
            return dao.persistBinFileInformation(binFileInfos,fileSrNo);
            
    }
	
}
