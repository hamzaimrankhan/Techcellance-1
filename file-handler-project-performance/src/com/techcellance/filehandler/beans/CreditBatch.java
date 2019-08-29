package com.techcellance.filehandler.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
public class CreditBatch implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private String IssuerCity = null ;
	private List<CreditBatchEntryRecord> batchEntryRecords = new ArrayList<CreditBatchEntryRecord>();
	public String getIssuerCity() {
		return IssuerCity;
	}
	public void setIssuerCity(String issuerCity) {
		IssuerCity = issuerCity;
	}
	public List<CreditBatchEntryRecord> getBatchEntryRecords() {
		return batchEntryRecords;
	}
	public void setBatchEntryRecords(List<CreditBatchEntryRecord> batchEntryRecords) {
		this.batchEntryRecords = batchEntryRecords;
	}
	
	
	
	
}
