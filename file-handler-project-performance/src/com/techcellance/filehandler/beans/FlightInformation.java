package com.techcellance.filehandler.beans;

import java.io.Serializable;

public class FlightInformation implements Serializable{

	
	private static final long serialVersionUID = 1L;
	private String departureAirport = null; 
	private String arrivalAirport = null; 
	private String stopOverCode = null; 
	private String flightcode = null;
	private String carrierCode = null; 
	private String fareClass = null; 
	private String fareBasis  =null;
	public String getDepartureAirport() {
		return departureAirport;
	}
	public void setDepartureAirport(String departureAirport) {
		this.departureAirport = departureAirport;
	}
	public String getArrivalAirport() {
		return arrivalAirport;
	}
	public void setArrivalAirport(String arrivalAirport) {
		this.arrivalAirport = arrivalAirport;
	}
	public String getStopOverCode() {
		return stopOverCode;
	}
	public void setStopOverCode(String stopOverCode) {
		this.stopOverCode = stopOverCode;
	}
	public String getFlightcode() {
		return flightcode;
	}
	public void setFlightCode(String flightcode) {
		this.flightcode = flightcode;
	}
	public String getCarrierCode() {
		return carrierCode;
	}
	public void setCarrierCode(String carrierCode) {
		this.carrierCode = carrierCode;
	}
	public String getFareClass() {
		return fareClass;
	}
	public void setFareClass(String fareClass) {
		this.fareClass = fareClass;
	}
	public String getFareBasis() {
		return fareBasis;
	}
	public void setFareBasis(String fareBasis) {
		this.fareBasis = fareBasis;
	}
	
	
	
	
	
}
