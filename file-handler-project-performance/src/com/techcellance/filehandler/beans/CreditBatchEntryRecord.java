package com.techcellance.filehandler.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.techcellance.filehandler.util.CommonUtils;

public class CreditBatchEntryRecord implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private String issuerCity = null;
	private String  invoiceNumber  = null;
	private String  invoiceDate = null;
	private String agentCode = null;
	private String authorizationId = null;
	private String responseCode = null;
	private String responseDescription = null;
	private String maskedCardNumber = null;
	
	private String  batchNumber = null;
	private String  documentNumber =null;
	private String  cardNumber = null; 
	private String cardType = null; 
	private String expiry = null;
	private String approvalCode = null; 
	private String passenger = null;
	private String ammount = null;
	private String currency = null ;
	private String ticketCode = null;
	private String passengerCode = null ;
	private String ticketRestricted = null;
	private String departureDate = null; 
	private String departureMonth = null;
	private String departureYear = null;
	private String fareClass = null;
	private String transactionType = null;
	private String airLineCode = null;
	private String airlineName = null; 
	private String invoiceName = null; 
	private String merchantAgreementId = null;
	private String countryCode = null; 
	private String legalEntityAddress = null; 
	private String legalEntityCity = null; 
	private String legalPostalCode = null; 
	private String legalCountryCode = null;
	private String midLookUpCode  = null ;
	private String status = null; 
	private List<String> missingMandatoryInfos = null; 
	private CreditBatchEntryAdditionalInformation entryAdditionalInformation = new CreditBatchEntryAdditionalInformation(); 
	
	
	public String getDepartureDate() {
		return departureDate;
	}
	public void setDepartureDate(String departureDate) {
		this.departureDate = departureDate;
	}
	public String getDepartureMonth() {
		return departureMonth;
	}
	public void setDepartureMonth(String departureMonth) {
		this.departureMonth = departureMonth;
	}
	public String getDepartureYear() {
		return departureYear;
	}
	public void setDepartureYear(String departureYear) {
		this.departureYear = departureYear;
	}
	
	
	public String getAuthorizationId() {
		return authorizationId;
	}
	public void setAuthorizationId(String authorizationId) {
		this.authorizationId = authorizationId;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getResponseDescription() {
		return responseDescription;
	}
	public void setResponseDescription(String responseDescription) {
		this.responseDescription = responseDescription;
	}
	public String getInvoiceNumber() {
		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {
		this.invoiceNumber = invoiceNumber;
	}
	public String getInvoiceDate() {
		return invoiceDate;
	}
	public void setInvoiceDate(String invoiceDate) {
		this.invoiceDate = invoiceDate;
	}
	public String getBatchNumber() {
		return batchNumber;
	}
	public void setBatchNumber(String batchNumber) {
		this.batchNumber = batchNumber;
	}
	public String getDocumentNumber() {
		return documentNumber;
	}
	public void setDocumentNumber(String documentNumber) {
		this.documentNumber = documentNumber;
	}
	public String getCardNumber() {
		return cardNumber;
	}
	public void setCardNumber(String cardNumber) {
		this.cardNumber = cardNumber;
	}
	
	public String getPassengerCode() {
		return passengerCode;
	}
	public void setPassengerCode(String passengerCode) {
		this.passengerCode = passengerCode;
	}
	public String getTicketRestricted() {
		return ticketRestricted;
	}
	public void setTicketRestricted(String ticketRestricted) {
		this.ticketRestricted = ticketRestricted;
	}
	
	public String getFareClass() {
		return fareClass;
	}
	
	public void setFareClass(String fareClass) {
		this.fareClass = fareClass;
	}
	
	public String getIssuerCity() {
		return issuerCity;
	}
	
	public void setIssuerCity(String issuerCity) {
		this.issuerCity = issuerCity;
	}
	
	public String getAgentCode() {
		return agentCode;
	}
	public void setAgentCode(String agentCode) {
		this.agentCode = agentCode;
	}
	public String getCardType() {
		return cardType;
	}
	public void setCardType(String cardType) {
		this.cardType = cardType;
	}
	public String getExpiry() {
		return expiry;
	}
	public void setExpiry(String expiry) {
		this.expiry = expiry;
	}
	public String getApprovalCode() {
		return approvalCode;
	}
	public void setApprovalCode(String approvalCode) {
		this.approvalCode = approvalCode;
	}
	public String getPassenger() {
		return passenger;
	}
	public void setPassenger(String passenger) {
		this.passenger = passenger;
	}
	public String getAmmount() {
		return ammount;
	}
	public void setAmmount(String ammount) {
		this.ammount = ammount;
	}
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	public String getTicketCode() {
		return ticketCode;
	}
	public void setTicketCode(String ticketCode) {
		this.ticketCode = ticketCode;
	}
	public String getMaskedCardNumber() {
		return maskedCardNumber;
	}
	public void setMaskedCardNumber(String maskedCardNumber) {
		this.maskedCardNumber = maskedCardNumber;
	}
	public String getTransactionType() {
		return transactionType;
	}
	public void setTransactionType(String transactionType) {
		this.transactionType = transactionType;
	}
	public CreditBatchEntryAdditionalInformation getEntryAdditionalInformation() {
		return entryAdditionalInformation;
	}
	public void setEntryAdditionalInformation(CreditBatchEntryAdditionalInformation entryAdditionalInformation) {
		this.entryAdditionalInformation = entryAdditionalInformation;
	}
	public String getAirLineCode() {
		return airLineCode;
	}
	public void setAirLineCode(String airLineCode) {
		this.airLineCode = airLineCode;
	}
	public String getAirlineName() {
		return airlineName;
	}
	public void setAirlineName(String airlineName) {
		this.airlineName = airlineName;
	}
	public String getInvoiceName() {
		return invoiceName;
	}
	public void setInvoiceName(String invoiceName) {
		this.invoiceName = invoiceName;
	}
	public String getMerchantAgreementId() {
		return merchantAgreementId;
	}
	public void setMerchantAgreementId(String merchantAgreementId) {
		this.merchantAgreementId = merchantAgreementId;
	}
	public String getCountryCode() {
		return countryCode;
	}
	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
	
	public String getLegalEntityAddress() {
		return legalEntityAddress;
	}
	public void setLegalEntityAddress(String legalEntityAddress) {
		this.legalEntityAddress = legalEntityAddress;
	}
	public String getLegalEntityCity() {
		return legalEntityCity;
	}
	public void setLegalEntityCity(String legalEntityCity) {
		this.legalEntityCity = legalEntityCity;
	}
	public String getLegalPostalCode() {
		return legalPostalCode;
	}
	public void setLegalPostalCode(String legalPostalCode) {
		this.legalPostalCode = legalPostalCode;
	}
	public String getLegalCountryCode() {
		return legalCountryCode;
	}
	public void setLegalCountryCode(String legalCountryCode) {
		this.legalCountryCode = legalCountryCode;
	}
	public String getMidLookUpCode() {
		return midLookUpCode;
	}
	public void setMidLookUpCode(String midLookUpCode) {
		this.midLookUpCode = midLookUpCode;
	}
	public List<String> getMissingMandatoryInfos() {
		
		if(CommonUtils.isNullOrEmptyCollection(missingMandatoryInfos)) {
			
			missingMandatoryInfos = new ArrayList<String>();
		}
		return missingMandatoryInfos;
	}
	public void setMissingMandatoryInfos(List<String> missingMandatoryInfos) {
		this.missingMandatoryInfos = missingMandatoryInfos;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}

}

