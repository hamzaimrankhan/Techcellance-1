package com.techcellance.filehandler.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreditInvoiceHeader implements Serializable  {

	private static final long serialVersionUID = 1L;
	private String  merchantAgreementId = null ;
	private String  InvoiceName = null; 
	private String  AirlineName = null;
	private String midLookupCode = null;
	private List<CreditBatch>  creditBatchs = new ArrayList<CreditBatch>();
	
	
	
	public String  getMerchantAgreementId() {
		return merchantAgreementId;
	}
	public void setMerchantAgreementId(String merchantAgreementId) {
		this.merchantAgreementId = merchantAgreementId;
	}
	public String getInvoiceName() {
		return InvoiceName;
	}
	public void setInvoiceName(String invoiceName) {
		InvoiceName = invoiceName;
	}
	public String getAirlineName() {
		return AirlineName;
	}
	public void setAirlineName(String airlineName) {
		AirlineName = airlineName;
	}
	public List<CreditBatch> getCreditBatchs() {
		return creditBatchs;
	}
	public void setCreditBatchs(List<CreditBatch> creditBatchs) {
		this.creditBatchs = creditBatchs;
	}
	public String getMidLookupCode() {
		return midLookupCode;
	}
	public void setMidLookupCode(String midLookupCode) {
		this.midLookupCode = midLookupCode;
	}
	
	
	
	
	
}



