package com.techcellance.filehandler.beans;

public class FileElementAttirbutes {

	private String elementName =  null;
	private int statrtPos ;
	private int length; 
	private String description  = null ;
	private String identifier = null;
	public int getStatrtPos() {
		return statrtPos;
	}
	
	public void setStatrtPos(int statrtPos) {
		this.statrtPos = statrtPos;
	}
	public int getLength() {
		return length;
	}
	@Override
	public String toString() {
		return "FileElementAttirbutes [statrtPos=" + statrtPos + ", length=" + length + ", description=" + description
				+ ", identifier=" + identifier + "]";
	}

	public void setLength(int length) {
		this.length = length;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

	public String getElementName() {
		return elementName;
	}

	public void setElementName(String elementName) {
		this.elementName = elementName;
	}
	
	
	
}
