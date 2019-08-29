package com.techcellance.filehandler.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CreditBatchEntryAdditionalInformation implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private  int  taxAmount = 0;
	private List<FlightInformation> flightInfos = new  ArrayList<FlightInformation>();
	private String currencyCode = null;
	
	
	public  int getTaxAmount() {
		return taxAmount;
	}
	public void setTaxAmount(int taxAmount) {
		this.taxAmount += taxAmount;
	}
	public List<FlightInformation> getFlightInfos() {
		return flightInfos;
	}
	public void setFlightInfos(List<FlightInformation> flightInfos) {
		this.flightInfos = flightInfos;
	}
	public String getCurrencyCode() {
		return currencyCode;
	}
	public void setCurrencyCode(String currencyCode) {
		this.currencyCode = currencyCode;
	}
 	
	
	
	
}
