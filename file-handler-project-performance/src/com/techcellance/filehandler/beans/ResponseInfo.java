package com.techcellance.filehandler.beans;

import java.io.Serializable;

public class ResponseInfo implements Serializable
{
	private static final long serialVersionUID = 1L;
	
	private String respCode = null;
	private String respDesc = null;
	private Object returnData = null;
	
	public ResponseInfo()
	{
	}
	
	public ResponseInfo(String respCode, String respDesc)
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

	public Object getReturnData() {
		return returnData;
	}

	public void setReturnData(Object returnData) {
		this.returnData = returnData;
	}

}