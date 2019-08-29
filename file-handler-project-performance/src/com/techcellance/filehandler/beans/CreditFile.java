package com.techcellance.filehandler.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreditFile implements Serializable {

	
	private static final long serialVersionUID = 1L;
	private Long fileSrNo = null ;
	private String fileName= null ; 
	List<CreditInvoiceHeader> creditInvoiceHeaders =  new ArrayList<CreditInvoiceHeader>();
	private Integer totalFailedRecords = 0 ;
	private Integer totalSuccessfullRecord = 0;
	private String fileType = null ;
	private List<String> alreadyProcessedOrderIds = new ArrayList<String>();
	private List<FailedEntryInfo> failedEntryInfos = new ArrayList<FailedEntryInfo>();
	
	private String fileStatus = null ;
	private String countryCode = null; 
	public Integer getTotalFailedRecords() {
		return totalFailedRecords;
	}
	public void setTotalFailedRecords(Integer totalFailedRecords) {
		this.totalFailedRecords = totalFailedRecords;
	}
	public Integer getTotalSuccessfullRecord() {
		return totalSuccessfullRecord;
	}
	public void setTotalSuccessfullRecord(Integer totalSuccessfullRecord) {
		this.totalSuccessfullRecord = totalSuccessfullRecord;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public List<CreditInvoiceHeader> getCreditInvoiceHeaders() {
		return creditInvoiceHeaders;
	}
	public void setCreditInvoiceHeaders(List<CreditInvoiceHeader> creditInvoiceHeaders) {
		this.creditInvoiceHeaders = creditInvoiceHeaders;
	}
	public String getFileType() {
		return fileType;
	}
	public void setFileType(String fileType) {
		this.fileType = fileType;
	}
	public List<String> getAlreadyProcessedOrderIds() {
		return alreadyProcessedOrderIds;
	}
	public void setAlreadyProcessedOrderIds(List<String> alreadyProcessedOrderIds) {
		this.alreadyProcessedOrderIds = alreadyProcessedOrderIds;
	}
	public Long getFileSrNo() {
		return fileSrNo;
	}
	public void setFileSrNo(Long fileSrNo) {
		this.fileSrNo = fileSrNo;
	}
	public String getFileStatus() {
		return fileStatus;
	}
	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public List<FailedEntryInfo> getFailedEntryInfos() {
		return failedEntryInfos;
	}
	
	public void setFailedEntryInfos(List<FailedEntryInfo> failedEntryInfos) {
		this.failedEntryInfos = failedEntryInfos;
	}

	public void addFailedEntryInList(FailedEntryInfo failedEntryInfo){
		this.failedEntryInfos.add(failedEntryInfo);
	} 


}
