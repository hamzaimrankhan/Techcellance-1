package com.techcellance.filehandler.beans;

import java.io.Serializable;

public class MerchantInformation implements Serializable {
	
	private static final long serialVersionUID = 1L;
	private int recNumber =0 ;
	private String captureOnlyAgent;
	private String merchantCode; 
	private String acquirerAccount;
	private String iATANumericalCarrierCode;
	private String accAccountCountryCode;
	private String currencyCode;
	private String paymentMethod;
	private String companyID;
	private String LegalEntityAddress1;
	private String legalEntityCity;
	private String legalentityPostCode;
	private String countryCode;
	
	public String getCaptureOnlyAgent() {
		return captureOnlyAgent;
	}
	public void setCaptureOnlyAgent(String captureOnlyAgent) {
		this.captureOnlyAgent = captureOnlyAgent;
	}
	public String getMerchantCode() {
		return merchantCode;
	}
	public void setMerchantCode(String merchantCode) {
		this.merchantCode = merchantCode;
	}
	public String getAcquirerAccount() {
		return acquirerAccount;
	}
	public void setAcquirerAccount(String acquirerAccount) {
		this.acquirerAccount = acquirerAccount;
	}
	public String getiATANumericalCarrierCode() {
		return iATANumericalCarrierCode;
	}
	public void setiATANumericalCarrierCode(String iATANumericalCarrierCode) {
		this.iATANumericalCarrierCode = iATANumericalCarrierCode;
	}
	public String getAccAccountCountryCode() {
		return accAccountCountryCode;
	}
	public void setAccAccountCountryCode(String accAccountCountryCode) {
		this.accAccountCountryCode = accAccountCountryCode;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public String getCompanyID() {
		return companyID;
	}
	public void setCompanyID(String companyID) {
		this.companyID = companyID;
	}
	public String getLegalEntityAddress1() {
		return LegalEntityAddress1;
	}
	public void setLegalEntityAddress1(String legalEntityAddress1) {
		LegalEntityAddress1 = legalEntityAddress1;
	}
	public String getLegalEntityCity() {
		return legalEntityCity;
	}
	public void setLegalEntityCity(String legalEntityCity) {
		this.legalEntityCity = legalEntityCity;
	}
	public String getLegalentityPostCode() {
		return legalentityPostCode;
	}
	public void setLegalentityPostCode(String legalentityPostCode) {
		this.legalentityPostCode = legalentityPostCode;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	public int getRecNumber() {
		return recNumber;
	}
	public void setRecNumber(int recNumber) {
		this.recNumber = recNumber;
	}
	
}
