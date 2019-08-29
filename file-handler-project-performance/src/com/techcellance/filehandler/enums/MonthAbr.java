package com.techcellance.filehandler.enums;

public enum MonthAbr {
	JANUARY("JAN", "01"),
	FEBUARY("FEB","02"),
	MARCH("MAR","03"),
	APRIL("APR","04"),
	MAY("MAY","05"),
	JUNE("JUN","06"),
	JULY("JUL","07"),
	AUGUST("AUG","08"),
	SEPTEMBER("SEP","09"),
	OCTOBER("OCT","10"),
	NOVEMBER("NOV","11"),
	DECEMBER("DEC","12");
	
	private String monthAbrv = null;
	private String monthNumeric= null;
	
	private MonthAbr(String monthAbr, String monthNumeric) {
		this.monthAbrv = monthAbr; 
		this.monthNumeric = monthNumeric;
	}

	public String getMonthAbrv() {
		return monthAbrv;
	}

	public void setMonthAbrv(String monthAbrv) {
		this.monthAbrv = monthAbrv;
	}

	public String getMonthNumeric() {
		return monthNumeric;
	}

	public void setMonthNumeric(String monthNumeric) {
		this.monthNumeric = monthNumeric;
	}

	
	public String getMonthInNumeric(String monthAbr) {
		
		String month = null;
		switch (monthAbr)
		{
		case "JAN":
			month = JANUARY.getMonthNumeric();
			break;
		case "FEB":
			month = FEBUARY.getMonthNumeric();
			break;
			
		case "MAR":
			month = MARCH.getMonthNumeric();
			break;
			
		case "APR":
			month = APRIL.getMonthNumeric();
			break;
			
		case "MAY":
			month = MAY.getMonthNumeric();
			break;
			
		case "JUN":
			month = JUNE.getMonthNumeric();
			break;
			
		case "JUL":
			month = JULY.getMonthNumeric();
			break;
			
		case "AUG":
			month = AUGUST.getMonthNumeric();
			break;
			
		case "SEP":
			month = SEPTEMBER.getMonthNumeric();
			break;
			
		case "OCT":
			month = OCTOBER.getMonthNumeric();
			break;
			
		case "NOV":
			month = NOVEMBER.getMonthNumeric();
			break;
			
		case "DEC":
			month = DECEMBER.getMonthNumeric();
			break;
		default:
			month= "";
			break;
		}
	
		
		return month;
	}

}

