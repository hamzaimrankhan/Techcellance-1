package com.techcellance.filehandler.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BinFile implements Serializable {

	private static final long serialVersionUID = 1L;

	private String fileName;
	private String fileStatus;
	private Integer totalSuccessfulRecord =0 ;
	private Integer totalFaioeldRecord = 0; 
	private List<BinInformation> binInformationList= new ArrayList<BinInformation>();
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
	public List<BinInformation> getBinInformationList() {
		return binInformationList;
	}
	public void setBinInformationList(List<BinInformation> binInformationList) {
		this.binInformationList = binInformationList;
	}
	public List<FailedEntryInfo> getFailedEntryInfos() {
		return failedEntryInfos;
	}
	public void setFailedEntryInfos(List<FailedEntryInfo> failedEntryInfos) {
		this.failedEntryInfos = failedEntryInfos;
	}
	
	
	
	
}
