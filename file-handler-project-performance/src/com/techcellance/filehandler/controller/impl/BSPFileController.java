package com.techcellance.filehandler.controller.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

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

				LGR.info(LGR.isInfoEnabled()
						? "Going to process the COMO file with file type : " + fileConfiguration.getFileType()
								+ " and file name = " + fileName
						: null);
				responseInfo = proccessFile(fileName);
				LGR.info(LGR.isInfoEnabled()
						? " Response recieved from file ( " + fileName + ") is " + responseInfo.getRespCode()
								+ " , response description: " + responseInfo.getRespDesc()
						: null);
				resetCount();
			}

		} catch (Exception e) {
			LGR.error("Exception occurred in BSPFileController.call  Exception : " + e.getMessage(), e);
			CommonUtils.populateResponseInfo(responseInfo, ResponseCode.SYSTEM_MALFUNCTION.getRespCode(),e.getMessage(), e);
			CompletableFuture.runAsync(() -> EmailGenerationHandler.generateEmailForSystemException(Constants.GEN_EMAIL_SYSTEM_EXCEPTION,e));
			return responseInfo;
		} finally {
		}

		CommonUtils.populateResponseInfo(responseInfo, ResponseCode.SUCCESS.getRespCode(),
				ResponseCode.SUCCESS.getRespDesc(), null);
		return responseInfo;

	}

	private ResponseInfo proccessFile(String fileName) throws Exception {

		boolean updateFilestatus = false;
		Long fileSrNo = null;
		ResponseInfo responseInfo = null;
		AbstractFileHandlerServiceDao dao = null;

		LGR.info(LGR.isInfoEnabled() ? "In BSP File Controller Task.processFile" : null);

		LGR.info(
				LGR.isInfoEnabled()
						? "Going to read the file(" + fileName
								+ ") from the SFTP server and parse it to capture the transaction with starting time: "
								+ CommonUtils.getCurrentFormatTime(Constants.DATE_TIME_FORMAT)
						: null);
		AbstractFileReaderTask fileReaderHandler = AbstractFileReaderTask.getInstance(fileConfiguration, fileName);
		responseInfo = fileReaderHandler.readAndValidateFile();
		LGR.info(
				LGR.isInfoEnabled()
						? " Response recieved from readAndParseFile : " + responseInfo.getRespCode()
								+ " response descritpion : " + responseInfo.getRespDesc()
						: null);

		if (!CommonUtils.isNullObject(responseInfo)
				&& responseInfo.getRespCode().equalsIgnoreCase(ResponseCode.SUCCESS.getRespCode())) {
			@SuppressWarnings("unchecked")
			List<CreditFile> creditFiles = (List<CreditFile>) (responseInfo.getReturnData());
			dao = AbstractFileHandlerServiceDao.getInstance();

//				credFileInHaltState = persistFileInformationAndCheckIfExistInHaltStatus(fileName);
//				if(!CommonUtils.isNullObject(credFileInHaltState)){
//					
//					fileSrNo = credFileInHaltState.getFileSrNo();
//					this.failedRecordCount.set( credFileInHaltState.getTotalFailedRecords());
//					this.successFulRecordCount.set(credFileInHaltState.getTotalSuccessfullRecord());				
//				}
//				else {			
			fileSrNo = dao.persistFile(fileName);
//				}

			// fileStatus =
			// processFileforSettlement(creditFiles,conn,fileSrNo,!CommonUtils.isNullObject(credFileInHaltState)?credFileInHaltState.getAlreadyProcessedOrderIds():null);
			processCreditFiles(creditFiles, fileSrNo);
			// fileStatus wala kaam rehta hia
			updateFilestatus = dao.updateFile(fileName, fileSrNo, this.successFulRecordCount, this.failedRecordCount,
					Constants.SUCCESSFUL_STATUS);

			if (updateFilestatus) {
				CommonUtils.populateResponseInfo(responseInfo, ResponseCode.SUCCESS.getRespCode(),
						ResponseCode.SUCCESS.getRespDesc(), null);
				// FTPUtil.moveProcessedFileFromSftpServer(fileConfiguration, fileName);
			} else {
				CommonUtils.populateResponseInfo(responseInfo, ResponseCode.FAIL.getRespCode(),
						ResponseCode.FAIL.getRespDesc(), null);
			}
		}

		LGR.info(
				LGR.isInfoEnabled()
						? "End of reading the file(" + fileName + ") from the SFTP server  with ending time: "
								+ CommonUtils.getCurrentFormatTime(Constants.DATE_TIME_FORMAT)
						: null);
		return responseInfo;
	}
//	private CreditFile persistFileInformationAndCheckIfExistInHaltStatus(String fileName) throws Exception {
//		
//		Connection conn=null; 
//		conn= DatabaseConnectionPool.getInstance().getConnection() ;
//		
//		if(null == conn){
//			
//			LGR.warn("Unable to get  the connection for database persistance");
//			return null;
//		}
//		
//		AbstractFileHandlerServiceDao fileHandlerServiceDao = AbstractFileHandlerServiceDao.getInstance();
//		return fileHandlerServiceDao.fetchFileIfExitInHaltState(fileName);
//		
//	}

//	private String processFileforSettlement(List<CreditFile> creditFiles, Connection conn, Long fileSrNo, List<String> alreadyProcessedOrderIds) throws ParserConfigurationException,Exception, TransformerException {
//		
//		Integer responseCode= -1;
//		String fileStatus = null;
//		PaymentServiceProcessor serviceProcessor = new PaymentServiceProcessor(); 
//		
//		
//		
//		
//		
//		for (CreditFile creditFile : creditFiles) {
//			
//			for (CreditInvoiceHeader crHeader : creditFile.getCreditInvoiceHeaders()) {
//				
//				for (CreditBatch batch : crHeader.getCreditBatchs()) {
//					
//					for (CreditBatchEntryRecord entry: batch.getBatchEntryRecords()) {
//					
//						if(!CommonUtils.isNullOrEmptyCollection(alreadyProcessedOrderIds) && alreadyProcessedOrderIds.contains(entry.getDocumentNumber()))
//						{
//							LGR.info(LGR.isInfoEnabled()?"Order Number ("+ entry.getDocumentNumber() + ") is already being processed for the file in halt status file sr no: " + fileSrNo +" ,so going to skip the record"  :null );
//							continue;
//						}
//					
//						serviceProcessor.processCreditBatchEntryRecord(entry,this.successFulRecordCount,this.failedRecordCount);		
//						responseCode =!CommonUtils.isNullOrEmptyString(entry.getResponseCode())? Integer.parseInt(entry.getResponseCode()):-1;
//						
//						if(responseCode == -1  ||  Constants.SERVICE_UNAVAILABLE_RESPONSE == responseCode ) {
//							LGR.info(LGR.isInfoEnabled() ?"Response recieved from world payment gateway is " + responseCode + " so going to set the status of file to Halt for later processing": null);
//							fileStatus = Constants.HALT_STATUS;
//							return fileStatus; 
//						}
//						
//						dao.persistCreditEntryRecord(fileConfiguration.getFileType(),fileSrNo , entry);
//						
//					}
//					
//				}
//				
//			}
//			fileStatus = Constants.SUCCESSFUL_STATUS;
//		}
//		
//		return fileStatus;
//	}

	@SuppressWarnings("rawtypes")
	private void processCreditFiles(List<CreditFile> creditFiles, Long fileSrNo) {
		try {

			List<CompletableFuture> processCreditFileFutureList = new ArrayList<>();
			CreditFileThreadPool.initializePool(Constants.CFH_THREAD_POOL_SIZE);
			for (CreditFile creditFile : creditFiles) {
				processCreditFileFutureList.add(CompletableFuture.runAsync(
						() -> processCreditEntries(creditFile, fileSrNo), CreditFileThreadPool.getThreadPool()));
			}

			CompletableFuture.allOf(
					processCreditFileFutureList.toArray(new CompletableFuture[processCreditFileFutureList.size()]))
					.get();

		} catch (Exception e) {
			LGR.error("##Exception## while processing credit entries: ", e);
		} finally {
			CreditFileThreadPool.shutDownPool();
		}

	}

	@SuppressWarnings("rawtypes")
	private void processCreditEntries(CreditFile creditFile, Long fileSrNo) {
		try {

			CreditEntryThreadPool.initializePool(Constants.CBR_THREAD_POOL_SIZE);

			List<CompletableFuture> processCreditEntryFutureList = new ArrayList<>();
			List<CreditBatchEntryRecord> crRecords = getallCreditEntryRecords(creditFile);
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
			CompletableFuture.allOf(
					processCreditEntryFutureList.toArray(new CompletableFuture[processCreditEntryFutureList.size()]))
					.get();
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

	private List<CreditBatchEntryRecord> getallCreditEntryRecords(CreditFile creditFile) throws Exception {
		List<CreditBatchEntryRecord> records = new ArrayList<CreditBatchEntryRecord>();

		for (CreditInvoiceHeader header : creditFile.getCreditInvoiceHeaders()) {
			for (CreditBatch batch : header.getCreditBatchs()) {
				records.addAll(batch.getBatchEntryRecords());
			}
		}
		return records;
	}

	private void resetCount() {

		this.successFulRecordCount.set(0);
		this.failedRecordCount.set(0);
	}

}