package com.techcellance.filehandler.beans;

import java.io.Serializable;

public class BinInformation implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private int recNumber  = 0 ;
	private Long rangeFrom = null ; 
	private Long rangeUntil = null;
	private String country = null;
	private String brand = null; 
	private String issuer = null; 
	private String family = null;
	public Long getRangeFrom() {
		return rangeFrom;
	}
	public void setRangeFrom(Long rangeFrom) {
		this.rangeFrom = rangeFrom;
	}
	public Long getRangeUntil() {
		return rangeUntil;
	}
	public void setRangeUntil(Long rangeUntil) {
		this.rangeUntil = rangeUntil;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getIssuer() {
		return issuer;
	}
	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
	public String getFamily() {
		return family;
	}
	public void setFamily(String family) {	
		this.family = family;
	}
	public int getRecNumber() {
		return recNumber;
	}
	public void setRecNumber(int recNumber) {
		this.recNumber = recNumber;
	} 
}