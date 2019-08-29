package com.techcellance.filehandler.enums;


public enum ResponseCode
{
	SUCCESS("00", "Success"),
	SERVICE_NOT_ALLOWED("01", "Service is not allowed"),
	SYSTEM_MALFUNCTION("96","System Malfunction"),
	SFTP_NOT_CONNECTED("02","System Malfunction"),
	FILE_READING_NOT_SUCCESSFUL("03","File Reading Exception"),
	COULD_NOT_MAKE_CONNECTION("04","Could not Make Cnnection"), 
	FAIL("05","fail"),
	FILE_ELEMENT_CONFIGURATION_NOT_DEFINED("10","File element configuration not defined"),
	NO_ERROR_AND_SUCCESS_TAG_IN_RESPONSE("11","No specified error or success tag found"),
	NO_FILE_FOUND_SFTP_FOLDER("12","No file found on sftp folder"),
	ILLEGAL_TRANSACTION_TYPE("13","Illegal transaction type"), EXCEPTION("14","Exception");
	
	
	private String respCode = null;
	private String respDesc = null;
	
	ResponseCode(String respCode, String respDesc)
	{
		this.respCode = respCode;
		this.respDesc = respDesc;
	}

	public String getRespCode() {
		return respCode;
	}

	public void setRespCode(String respCode) {
		this.respCode = respCode;
	}

	public String getRespDesc() {
		return respDesc;
	}

	public void setRespDesc(String respDesc) {
		this.respDesc = respDesc;
	}
}