package com.techcellance.filehandler.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.techcellance.filehandler.util.CommonUtils;

public class FailedEntryInfo implements Serializable{


	private static final long serialVersionUID = 1L;
	private int entryRecNo = 0 ;
	private List<String> missingColumns = new ArrayList<String>();

	public List<String> getMissingColumns() {
		return missingColumns;
	}

	public void setMissingColumns(List<String> missingColumns) {
		this.missingColumns = missingColumns;
	}

	public int getNoOfMissingColumns(){
		return missingColumns.size();
	}
	
	public String getCommaSeperatedMissingColumns(){
		
		return !CommonUtils.isNullOrEmptyCollection(missingColumns) ?String.join(",", missingColumns): null;
	}

	public int getEntryRecNo() {
		return entryRecNo;
	}

	public void setEntryRecNo(int entryRecNo) {
		this.entryRecNo = entryRecNo;
	}

}
