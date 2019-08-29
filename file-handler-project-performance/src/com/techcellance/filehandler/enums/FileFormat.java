package com.techcellance.filehandler.enums;

public enum FileFormat {
	BSP("B", "BSP",1),BIN_FILE("BF","BIN FILE",2) ,MID_LOOKUP("MID", "MID LOOKUP FILE", 3);
	  
	private String formatId = null;
	private String formatName = null;
	private int fileType = 0 ;
	
	FileFormat(String formatId, String formatName,int fileType)
	{
		this.formatId = formatId;
		this.formatName = formatName;
		this.fileType = fileType ;
	}
	
	public static FileFormat getFileFormatByFormatId(int fileType)
	{
		for(FileFormat fileFormat : values())
		{
			if(fileFormat.getFileType() == fileType)
			{
				return fileFormat;
			}
		}
		
		return null;
	}

	public String getFormatId() {
		return formatId;
	}

	public void setFormatId(String formatId) {
		this.formatId = formatId;
	}

	public String getFormatName() {
		return formatName;
	}

	public void setFormatName(String formatName) {
		this.formatName = formatName;
	}

	public int getFileType() {
		return fileType;
	}

	public void setFileType(int fileType) {
		this.fileType = fileType;
	}
}
