package com.techcellance.filehandler.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techcellance.filehandler.Email.EmailGenerationHandler;
import com.techcellance.filehandler.beans.CreditBatch;
import com.techcellance.filehandler.beans.CreditBatchEntryRecord;
import com.techcellance.filehandler.beans.CreditFile;
import com.techcellance.filehandler.beans.CreditInvoiceHeader;
import com.techcellance.filehandler.beans.FileConfiguration;
import com.techcellance.filehandler.beans.ResponseInfo;
import com.techcellance.filehandler.bl.PaymentServiceProcessor;
import com.techcellance.filehandler.controller.AbstractFileController;
import com.techcellance.filehandler.dao.AbstractFileHandlerServiceDao;
import com.techcellance.filehandler.enums.ResponseCode;
import com.techcellance.filehandler.reader.AbstractFileReaderTask;
import com.techcellance.filehandler.threadpool.CreditEntryThreadPool;
import com.techcellance.filehandler.threadpool.CreditFileThreadPool;
import com.techcellance.filehandler.util.CommonUtils;
import com.techcellance.filehandler.util.Constants;
import com.techcellance.filehandler.util.FTPUtil;

public class BSPFileController extends AbstractFileController {
	private static Logger LGR = LogManager.getLogger(BSPFileController.class);
	private FileConfiguration fileConfiguration = null;
	AtomicInteger successFulRecordCount = new AtomicInteger(0);
	AtomicInteger failedRecordCount = new AtomicInteger(0);

	public BSPFileController(FileConfiguration fileConfiguration) {
		this.fileConfiguration = fileConfiguration;
	}

	@Override
	public ResponseInfo processFiles() {
		ResponseInfo responseInfo = new ResponseInfo();
		List<String> files = null;

		try {
			LGR.info("Going to pick the all files from the sftp source destination");
			files = FTPUtil.getListOfFilesFromSftpServer(fileConfiguration);
			if (!CommonUtils.isNullOrEmptyCollection(files)) {
				CommonUtils.removeAlreadyPocessedFilesFromTheList(files);
			}

			if (CommonUtils.isNullOrEmptyCollection(files)) {

				LGR.info(LGR.isInfoEnabled()
						? "No files found on following location " + fileConfiguration.getSoucrePath()
						: null);
				CommonUtils.populateResponseInfo(responseInfo, ResponseCode.NO_FILE_FOUND_SFTP_FOLDER.getRespCode(),
						ResponseCode.NO_FILE_FOUND_SFTP_FOLDER.getRespDesc(), null);
				return responseInfo;
			}

			for (String fileName : files) {

				LGR.info(LGR.isInfoEnabled()? "Going to process the COMO file with file type : " + fileConfiguration.getFileType()+ " and file name = " + fileName: null);
				responseInfo = proccessFile(fileName);
				LGR.info(LGR.isInfoEnabled()? " Response recieved from file ( " + fileName + ") is " + responseInfo.getRespCode()+ " , response description: " + responseInfo.getRespDesc(): null);
				resetCount();
			}

		} catch (Exception e) {
			LGR.error("Exception occurred in BSPFileController.call  Exception : " + e.getMessage(), e);
			CommonUtils.populateResponseInfo(responseInfo, ResponseCode.SYSTEM_MALFUNCTION.getRespCode(),e.getMessage(), e);
			CompletableFuture.runAsync(() -> EmailGenerationHandler.generateEmailForSystemException(Constants.GEN_EMAIL_SYSTEM_EXCEPTION,e));
			return responseInfo;
		} finally {
		}

		CommonUtils.populateResponseInfo(responseInfo, ResponseCode.SUCCESS.getRespCode(),ResponseCode.SUCCESS.getRespDesc(), null);
		return responseInfo;

	}

	private ResponseInfo proccessFile(String fileName) throws Exception {

		boolean updateFilestatus = false;
		Long fileSrNo = null;
		ResponseInfo responseInfo = null;
		AbstractFileHandlerServiceDao dao = null;
		String fileStatus= null ;
		List<String> inProgressOrdernumber = new ArrayList<String>();
		LGR.info(LGR.isInfoEnabled() ? "In BSP File Controller Task.processFile" : null);

		LGR.info(
				LGR.isInfoEnabled()? "Going to read the file(" + fileName+ ") from the SFTP server and parse it to capture the transaction with starting time: "+ CommonUtils.getCurrentFormatTime(Constants.DATE_TIME_FORMAT): null);
		AbstractFileReaderTask fileReaderHandler = AbstractFileReaderTask.getInstance(fileConfiguration, fileName);
		responseInfo = fileReaderHandler.readAndValidateFile();
		LGR.info(LGR.isInfoEnabled()? " Response recieved from readAndParseFile : " + responseInfo.getRespCode()+ " response descritpion : " + responseInfo.getRespDesc(): null);

		if (!CommonUtils.isNullObject(responseInfo)
				&& responseInfo.getRespCode().equalsIgnoreCase(ResponseCode.SUCCESS.getRespCode())) {
			@SuppressWarnings("unchecked")
			List<CreditFile> creditFiles = (List<CreditFile>) (responseInfo.getReturnData());
			dao = AbstractFileHandlerServiceDao.getInstance();
 
			CreditFile credFileInHaltState = AbstractFileHandlerServiceDao.getInstance().fetchFileIfExitInHaltState(fileName);
			if(!CommonUtils.isNullObject(credFileInHaltState)){		
					fileSrNo = credFileInHaltState.getFileSrNo();
					this.failedRecordCount.set( credFileInHaltState.getTotalFailedRecords());
					this.successFulRecordCount.set(credFileInHaltState.getTotalSuccessfullRecord());				
					inProgressOrdernumber= credFileInHaltState.getInProgressOrderNumbers();
			}
			else {			
				fileSrNo = dao.persistFile(fileName);
			}

			processCreditFiles(creditFiles, fileSrNo,inProgressOrdernumber);
			fileStatus = creditFiles.stream().anyMatch(creditFile -> Constants.HALT_STATUS.equalsIgnoreCase(creditFile.getFileStatus()))?Constants.HALT_STATUS:Constants.SUCCESSFUL_STATUS;	
			
			updateFilestatus = dao.updateFile(fileName, fileSrNo, this.successFulRecordCount, this.failedRecordCount,fileStatus);
			
			if (updateFilestatus) {
				CommonUtils.populateResponseInfo(responseInfo, ResponseCode.SUCCESS.getRespCode(),
						ResponseCode.SUCCESS.getRespDesc(), null);
			} else {
				CommonUtils.populateResponseInfo(responseInfo, ResponseCode.FAIL.getRespCode(),
						ResponseCode.FAIL.getRespDesc(), null);
			}
			
			if(Constants.HALT_STATUS.equalsIgnoreCase(fileStatus)){
				generateEmailforMisingMIDAgent(creditFiles,fileName);
			}
			
		}

		LGR.info(
				LGR.isInfoEnabled()
						? "End of reading the file(" + fileName + ") from the SFTP server  with ending time: "
								+ CommonUtils.getCurrentFormatTime(Constants.DATE_TIME_FORMAT)
						: null);
		return responseInfo;
	}
	
	private void generateEmailforMisingMIDAgent(List<CreditFile> creditFiles,String fileName) {
		List<CreditBatchEntryRecord> records =  getInProgressRecordsWithMissingMidAgentInformation(creditFiles);
		CompletableFuture.runAsync(() -> EmailGenerationHandler.generateEmailForMissingMIDInformation(Constants.GEN_EMAIL_MISSING_MID_AGENT,records,fileName));	
	}

	private List<CreditBatchEntryRecord> getInProgressRecordsWithMissingMidAgentInformation(List<CreditFile> creditFiles) {
		
		List<CreditBatchEntryRecord> records = new ArrayList<CreditBatchEntryRecord>(); 
		
		for (CreditFile creditFile : creditFiles) {
			for (CreditInvoiceHeader header : creditFile.getCreditInvoiceHeaders()) {
				for (CreditBatch batch : header.getCreditBatchs()) {
					records.addAll(batch.getBatchEntryRecords().stream().filter(entry-> Constants.IN_PROGRESS.equalsIgnoreCase(entry.getStatus())).collect(Collectors.toList()));
				}
			}
		}
		
		return records;
	}

	@SuppressWarnings("rawtypes")
	private void processCreditFiles(List<CreditFile> creditFiles, Long fileSrNo, List<String> inProgressOrdernumber) {
		try {

			List<CompletableFuture> processCreditFileFutureList = new ArrayList<>();
			CreditFileThreadPool.initializePool(Constants.CFH_THREAD_POOL_SIZE);
			for (CreditFile creditFile : creditFiles) {
				processCreditFileFutureList.add(CompletableFuture.runAsync(() -> processCreditEntries(creditFile, fileSrNo,inProgressOrdernumber), CreditFileThreadPool.getThreadPool()));
			}
			CompletableFuture.allOf(processCreditFileFutureList.toArray(new CompletableFuture[processCreditFileFutureList.size()])).get();
	
			
		} catch (Exception e) {
			LGR.error("##Exception## while processing credit entries: ", e);
		} finally {
			CreditFileThreadPool.shutDownPool();
		}

	}

	@SuppressWarnings("rawtypes")
	private void processCreditEntries(CreditFile creditFile, Long fileSrNo, List<String> inProgressOrdernumber) {
		try {

			CreditEntryThreadPool.initializePool(Constants.CBR_THREAD_POOL_SIZE);

			List<CompletableFuture> processCreditEntryFutureList = new ArrayList<>();
			List<CreditBatchEntryRecord> crRecords = getallCreditEntryRecords(creditFile,inProgressOrdernumber);
			AbstractFileHandlerServiceDao dao = AbstractFileHandlerServiceDao.getInstance();
	
			for (CreditBatchEntryRecord record : crRecords) {
				processCreditEntryFutureList.add(CompletableFuture.runAsync(() -> {
					try {
						processCreditBatchEntryRecord(record);
					} catch (Exception e) {
						LGR.error("##Exception## while processing credit entries: ", e);

					}
				}, CreditEntryThreadPool.getThreadPool()));
			}
			CompletableFuture.allOf(processCreditEntryFutureList.toArray(new CompletableFuture[processCreditEntryFutureList.size()])).get();
			
			if (crRecords.stream().anyMatch(entry -> Constants.IN_PROGRESS.equalsIgnoreCase(entry.getStatus()))) {
				creditFile.setFileStatus(Constants.HALT_STATUS);
			} else {
				creditFile.setFileStatus(Constants.SUCCESSFUL_STATUS);
			}
			
			dao.persistCreditEntryRecord(fileConfiguration.getFileType(), fileSrNo, crRecords);

		} catch (Exception ex) {
			LGR.error("Exception in processCreditEntries, ", ex);

		} finally {

			CreditEntryThreadPool.shutDownPool();
		}
	}

	private void processCreditBatchEntryRecord(CreditBatchEntryRecord record) {

		PaymentServiceProcessor.getInstance().processCreditBatchEntryRecord(record, this.successFulRecordCount,
				this.failedRecordCount);
	}

	private List<CreditBatchEntryRecord> getallCreditEntryRecords(CreditFile creditFile, List<String> inProgressOrdernumber) throws Exception {
		List<CreditBatchEntryRecord> records = new ArrayList<CreditBatchEntryRecord>();

		for (CreditInvoiceHeader header : creditFile.getCreditInvoiceHeaders()) {
			for (CreditBatch batch : header.getCreditBatchs()) {
				records.addAll(batch.getBatchEntryRecords());
			}
		}
		records = records.stream().filter(record -> !inProgressOrdernumber.contains(record.getDocumentNumber())).collect(Collectors.toList());
		return records;
	}

	private void resetCount() {

		this.successFulRecordCount.set(0);
		this.failedRecordCount.set(0);
	}

}