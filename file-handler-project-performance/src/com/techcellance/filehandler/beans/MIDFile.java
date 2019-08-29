package com.techcellance.filehandler.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MIDFile implements Serializable{

	private static final long serialVersionUID = 1L;

	private String fileName;
	private String fileStatus;
	private Integer totalSuccessfulRecord =0 ;
	private Integer totalFaioeldRecord = 0; 
	private List<MerchantInformation> merchantInformationList= new ArrayList<MerchantInformation>();
	private List<FailedEntryInfo> failedEntryInfos = new ArrayList<FailedEntryInfo>();
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getFileStatus() {
		return fileStatus;
	}
	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}
	public Integer getTotalSuccessfulRecord() {
		return totalSuccessfulRecord;
	}
	public void setTotalSuccessfulRecord(Integer totalSuccessfulRecord) {
		this.totalSuccessfulRecord = totalSuccessfulRecord;
	}
	public Integer getTotalFaioeldRecord() {
		return totalFaioeldRecord;
	}
	public void setTotalFaioeldRecord(Integer totalFaioeldRecord) {
		this.totalFaioeldRecord = totalFaioeldRecord;
	}
	public List<MerchantInformation> getMerchantInformationList() {
		return merchantInformationList;
	}
	public void setMerchantInformationList(List<MerchantInformation> binInformationList) {
		this.merchantInformationList = binInformationList;
	}
	public List<FailedEntryInfo> getFailedEntryInfos() {
		return failedEntryInfos;
	}
	public void setFailedEntryInfos(List<FailedEntryInfo> failedEntryInfos) {
		this.failedEntryInfos = failedEntryInfos;
	}

	
		
}
