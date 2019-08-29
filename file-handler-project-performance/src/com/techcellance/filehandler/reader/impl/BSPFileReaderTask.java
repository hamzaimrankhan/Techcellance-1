package com.techcellance.filehandler.reader.impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.techcellance.filehandler.beans.CreditBatch;
import com.techcellance.filehandler.beans.CreditBatchEntryRecord;
import com.techcellance.filehandler.beans.CreditFile;
import com.techcellance.filehandler.beans.CreditInvoiceHeader;
import com.techcellance.filehandler.beans.FileConfiguration;
import com.techcellance.filehandler.beans.FlightInformation;
import com.techcellance.filehandler.beans.ResponseInfo;
import com.techcellance.filehandler.bl.FileAttributeController;
import com.techcellance.filehandler.enums.EntryRecordAttribute;
import com.techcellance.filehandler.enums.FileFormat;
import com.techcellance.filehandler.enums.ResponseCode;
import com.techcellance.filehandler.reader.AbstractFileReaderTask;
import com.techcellance.filehandler.util.CommonUtils;
import com.techcellance.filehandler.util.Constants;
import com.techcellance.filehandler.util.FTPUtil;

public class BSPFileReaderTask extends AbstractFileReaderTask
{
	private static Logger LGR = LogManager.getLogger(BSPFileReaderTask.class);
	private String fileName  = null ;
	private FileConfiguration  fileConfiguration = null ;
	private FileAttributeController fileAttributeController= null;
	
	public BSPFileReaderTask( FileConfiguration fileConfiguration , String fileName)
	{
		
		this.fileName = fileName; 
		this.fileConfiguration = fileConfiguration;
	}
	

	@Override
	public ResponseInfo readAndValidateFile() throws Exception{
	
		ResponseInfo responseInfo = new ResponseInfo();
		BufferedReader br = null;
		List<CreditFile> creditFiles = null ;
		ChannelSftp channelSftp= null;
		Session session= null ;
		try {
	
		fileAttributeController = new FileAttributeController(FileFormat.BSP.getFileType());
		
		if(CommonUtils.isNullOrEmptyMap(fileAttributeController.getFileAttributes())) {
				
			CommonUtils.populateResponseInfo(responseInfo,ResponseCode.FILE_ELEMENT_CONFIGURATION_NOT_DEFINED.getRespCode(),ResponseCode.FILE_ELEMENT_CONFIGURATION_NOT_DEFINED.getRespDesc(), null);
			return responseInfo;			
		}
		
		
		session = FTPUtil.getSession(fileConfiguration);
		if(null == session) {
				LGR.info(LGR.isInfoEnabled()? " Could  not get the session  so unable to fetch the list of files" : null );
				CommonUtils.populateResponseInfo(responseInfo,ResponseCode.COULD_NOT_MAKE_CONNECTION.getRespCode(),ResponseCode.COULD_NOT_MAKE_CONNECTION.getRespDesc(), null);
				return responseInfo;			
		}
		channelSftp = FTPUtil.getSFTPChannel(session);
		
		br = FTPUtil.readFileFromSftpServer(fileConfiguration, fileName,channelSftp);		
	
		//Temporary Code 
		if(!CommonUtils.isNullOrEmptyString(Constants.TEMP_FILE_PATH )){
		
			LGR.info(LGR.isInfoEnabled()? "INFO: This feature is temporary ON for testing puprpose, so going to read file from path : " + Constants.TEMP_FILE_PATH:null);
			FileReader fr = null;
			fr = new FileReader(Constants.TEMP_FILE_PATH);
			br = new BufferedReader(fr);
			
		}
		
		
		if(!CommonUtils.isNullObject(br)){
			LGR.info(LGR.isInfoEnabled() ? "Going to read and parse file":null );
			creditFiles = readfileAndParseData(br);	
		}
		
		if(!CommonUtils.isNullOrEmptyCollection(creditFiles)) {
		CommonUtils.populateResponseInfo(responseInfo, ResponseCode.SUCCESS.getRespCode(), ResponseCode.SUCCESS.getRespDesc(), creditFiles);
		
		}
		else {
		CommonUtils.populateResponseInfo(responseInfo, ResponseCode. FILE_READING_NOT_SUCCESSFUL.getRespCode(), ResponseCode.FILE_READING_NOT_SUCCESSFUL.getRespDesc(), null);	
		}

		}finally
		{
			CommonUtils.closeBufferReader(br);
				FTPUtil.closeSFTPChannel(channelSftp);
				FTPUtil.closeSession(session);
	    
		}
		return responseInfo;
	}
	
	
	
	private List<CreditFile> readfileAndParseData(BufferedReader br) throws NumberFormatException, Exception {
		
		String  line = null;
		List<CreditFile> creditFiles = new ArrayList<CreditFile>();
			
		line = readLine(br);
		LGR.debug(LGR.isDebugEnabled()? "Line read ---> " + line:null);
		
		if((null == line) || !(line.trim().startsWith(Constants.FILE_HEADER_IDENTIFIER) )){
			LGR.debug("File not according to BSP Format Format. File : " + fileName);
			return null;
		}
		
		
		while(null != line){
		
			if(line.trim().startsWith(Constants.FILE_HEADER_IDENTIFIER) )
			{
				CreditFile creditFile = new CreditFile();
				creditFile.setFileName(fileName);
				creditFile.setCountryCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.CountryCode.name()));
				while(null !=line && !line.trim().startsWith(Constants.FILE_TRAILER_IDENTIFIER)) {
	
					line= readLine(br);				
					LGR.debug(LGR.isDebugEnabled()? "Line read ---> " + line:null);
					
					
					if(line.trim().startsWith(Constants.INVOICE_HEADER_IDENTIFIER))
					{
					CreditInvoiceHeader  creditInvoiceHeader = new CreditInvoiceHeader();		
					line = readInvoiceBatch(creditInvoiceHeader,br,creditFile,line );
					creditFile.getCreditInvoiceHeaders().add(creditInvoiceHeader);
					}
					
					
				}	
				
				creditFiles.add(creditFile);
			}
			
			line = readLine(br);
			LGR.debug(LGR.isDebugEnabled()? "Line read ---> " + line:null);
			
		}
		
		return creditFiles;
		
		
	}

	public String readLine(BufferedReader bf) throws IOException
	{
		String temp = "";
		   
		while(true)
		{
			temp = bf.readLine();
			
			if((temp == null) || (temp.trim().length() > 1))
			{
				break;
			}
		}
		   
		return temp;
	}
	
	public String readInvoiceBatch(CreditInvoiceHeader creditInvoiceHeader, BufferedReader bf, CreditFile creditFile,String line) throws NumberFormatException, Exception
	{
			
		populateInvoiceHeaderInformation(creditInvoiceHeader , line );
		
		while(((null != line) && (!line.startsWith(Constants.INVOICE_TRAILER_IDENTIFIER))))
		{
			line = readLine(bf);
			LGR.debug(LGR.isDebugEnabled()? "Line read ---> " + line:null);
			
			if(null != line && line.startsWith(Constants.BATCH_HEADER_IDENTIFIER)) {
				CreditBatch creditBatch = new CreditBatch() ;
				
				populateBatchInfo(creditBatch,line);
				line = readLine(bf);
				LGR.debug(LGR.isDebugEnabled()? "Line read ---> " + line:null);
				
				List<CreditBatchEntryRecord> creditBatchEntryRecords = new ArrayList<CreditBatchEntryRecord>();
				while(null ==line || !line.startsWith(Constants.BATCH_TRAILER_IDENTIFIER)) {
					
					
					if(null != line && line.startsWith(Constants.TRANSACTION_BASIC_RECORD_IDENTIFIER)) {
						
						CreditBatchEntryRecord  creditBatchEntryRecord = new CreditBatchEntryRecord();
						
						line = populateBatchEntryRecord(creditBatchEntryRecord , line,bf); 
						if(CommonUtils.isNullOrEmptyString(creditBatchEntryRecord.getDocumentNumber()))
						{
							continue; 
						}
						creditBatchEntryRecord.setCountryCode(creditFile.getCountryCode());
						creditBatchEntryRecord.setIssuerCity(creditBatch.getIssuerCity());
						creditBatchEntryRecord.setAirlineName(creditInvoiceHeader.getAirlineName());
						creditBatchEntryRecord.setInvoiceName(creditInvoiceHeader.getInvoiceName());
						creditBatchEntryRecord.setMerchantAgreementId(creditInvoiceHeader.getMerchantAgreementId());
						creditBatchEntryRecords.add(creditBatchEntryRecord);
					}
				
			}
				
				creditBatch.setBatchEntryRecords(creditBatchEntryRecords);
				
				creditInvoiceHeader.getCreditBatchs().add(creditBatch);
		}
		
		}
		
		return line;
	}


	private String populateBatchEntryRecord(CreditBatchEntryRecord creditBatchEntryRecord, String line, BufferedReader bf)throws NumberFormatException, Exception {

		String transactionType = fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.TransactionType.name());
		String invoiceNumber   =  fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.InvoiceNumber.name());		
		creditBatchEntryRecord.setTransactionType(transactionType);
		creditBatchEntryRecord.setAmmount(CommonUtils.removeLeadingZeors(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.Ammount.name())));
		creditBatchEntryRecord.setCurrency(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.Currency.name()));	
		creditBatchEntryRecord.setDocumentNumber(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.DocumentNumber.name()));
		creditBatchEntryRecord.setCardType(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.CardType.name()));
		creditBatchEntryRecord.setCardNumber(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.CardNumber.name()));
		creditBatchEntryRecord.setExpiry(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.Expiry.name()));
		creditBatchEntryRecord.setCardType(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.CardType.name()));
		creditBatchEntryRecord.setCardNumber(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.CardNumber.name()));
		creditBatchEntryRecord.setExpiry(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.Expiry.name()));
		creditBatchEntryRecord.setPassenger(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.Passenger.name()));
		
		if(Constants.DEBIT_TRANSACTION.equalsIgnoreCase(transactionType)){
				
		creditBatchEntryRecord.setInvoiceNumber(invoiceNumber);
		creditBatchEntryRecord.setInvoiceDate(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.InvoiceDate.name()));
		creditBatchEntryRecord.setAgentCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.AgentCode.name()));
		creditBatchEntryRecord.setBatchNumber(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.BatchNumber.name()));
		creditBatchEntryRecord.setApprovalCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.ApprovalCode.name()));
		creditBatchEntryRecord.setPassengerCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.PassengerCode.name()));
		creditBatchEntryRecord.setTicketCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.TicketCode.name()));
		creditBatchEntryRecord.setTicketRestricted(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.TicketRestricted.name()));
		if(CommonUtils.isNullOrEmptyString(creditBatchEntryRecord.getTicketRestricted()) || !creditBatchEntryRecord.getTicketRestricted().equals("1")) {
			creditBatchEntryRecord.setTicketRestricted("0");
		}
		creditBatchEntryRecord.setDepartureDate(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.DepartureDate.name()));
		creditBatchEntryRecord.setDepartureMonth(CommonUtils.getMonthInNumeric(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.DepartureMonth.name())));
		creditBatchEntryRecord.setDepartureYear(CommonUtils.getDepartureDateYear(creditBatchEntryRecord.getDepartureMonth(),creditBatchEntryRecord.getDepartureDate()));
		}
		
		line  = readLine(bf);
		LGR.debug(LGR.isDebugEnabled()? "Line read ---> " + line:null);
		
		while (line!= null )
		{
			if(line.trim().startsWith(Constants.BATCH_TRAILER_IDENTIFIER) || line.trim().startsWith(Constants.TRANSACTION_BASIC_RECORD_IDENTIFIER)  ){
				break;
			}	
			else if(line.trim().startsWith(Constants.TRANSACTION_OPTIONAL_RECORD_IDENTIFIER) && !Constants.CREDIT_TRANSACTION.equalsIgnoreCase(transactionType)){
				populateEntryOptionalRecord(creditBatchEntryRecord ,line);
			}
			else if(line.trim().startsWith(Constants.TRANSACTION_OPTIONAL_TAX_RECORD_IDENTIFIER) && !Constants.CREDIT_TRANSACTION.equalsIgnoreCase(transactionType)) {
				populateEntryOptionalTaxRecord(creditBatchEntryRecord, line);
			}
			
			line = readLine(bf);
			LGR.debug(LGR.isDebugEnabled()? "Line read ---> " + line:null);
		}
		
		return line ;
		
		
	}


	private void populateEntryOptionalTaxRecord(CreditBatchEntryRecord creEntryRecord , String line)throws NumberFormatException,Exception {
	
		if(CommonUtils.isNullOrEmptyString(creEntryRecord.getEntryAdditionalInformation().getCurrencyCode())) {
		
			creEntryRecord.getEntryAdditionalInformation().setCurrencyCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.CurrencyCode.name()));
		}
		
		Double  amount = Double.parseDouble(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.TaxAmount1.name()))
				           +Double.parseDouble(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.TaxAmount2.name())) 
				           +Double.parseDouble(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.TaxAmount3.name()))
				           +Double.parseDouble(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.TaxAmount4.name()))
				           +Double.parseDouble(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.TaxAmount5.name()))
				           +Double.parseDouble(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.TaxAmount6.name()))
				           +Double.parseDouble(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.TaxAmount7.name()))
				           +Double.parseDouble(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.TaxAmount8.name()))
				           +Double.parseDouble(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.TaxAmount9.name()))
				           +Double.parseDouble(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.TaxAmount10.name()));
		
		
		creEntryRecord.getEntryAdditionalInformation().setTaxAmount(amount.intValue());
	}


	private void populateEntryOptionalRecord(CreditBatchEntryRecord credEntryRecord,String line) throws Exception{

		
		if(!CommonUtils.isNullOrEmptyString(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.DepartureAirport.name()))) {
			FlightInformation  flightInformation  = new FlightInformation();
			credEntryRecord.setAirLineCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.ArilineCode.name()));
			
			flightInformation.setCarrierCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.CarrierCode.name()));
			flightInformation.setStopOverCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.StopOverCode.name()));
			flightInformation.setDepartureAirport(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.DepartureAirport.name()));
			flightInformation.setArrivalAirport(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.ArrivalAirport.name()));
			flightInformation.setFlightCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.FlightCode.name()));
			flightInformation.setFareClass(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.FareClass.name()));
			flightInformation.setFareBasis(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.FareBasis.name()).replaceAll("[^a-zA-Z0-9]", ""));
						
			credEntryRecord.getEntryAdditionalInformation().getFlightInfos().add(flightInformation);	
		}
		
		if(!CommonUtils.isNullOrEmptyString(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.DepartureAirport_2.name()))){
			FlightInformation  flightInformation  = new FlightInformation();
			
			flightInformation.setCarrierCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.CarrierCode_2.name()));
			flightInformation.setStopOverCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.StopOverCode_2.name()));
			flightInformation.setDepartureAirport(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.DepartureAirport_2.name()));
			flightInformation.setArrivalAirport(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.ArrivalAirport_2.name()));
			flightInformation.setFlightCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.FlightCode_2.name()));
			flightInformation.setFareClass(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.FareClass_2.name()));
			flightInformation.setFareBasis(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.FareBasis_2.name()).replaceAll("[^a-zA-Z0-9]", ""));
			
			credEntryRecord.getEntryAdditionalInformation().getFlightInfos().add(flightInformation);
		}
		

		if(!CommonUtils.isNullOrEmptyString(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.DepartureAirport_3.name()))){
			FlightInformation  flightInformation  = new FlightInformation();
			
			flightInformation.setCarrierCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.CarrierCode_3.name()));
			flightInformation.setStopOverCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.StopOverCode_3.name()));
			flightInformation.setDepartureAirport(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.DepartureAirport_3.name()));
			flightInformation.setArrivalAirport(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.ArrivalAirport_3.name()));
			flightInformation.setFlightCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.FlightCode_3.name()));
			flightInformation.setFareClass(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.FareClass_3.name()));
			flightInformation.setFareBasis(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.FareBasis_3.name()).replaceAll("[^a-zA-Z0-9]", ""));
			
			credEntryRecord.getEntryAdditionalInformation().getFlightInfos().add(flightInformation);
		}
		
		
		
		if(!CommonUtils.isNullOrEmptyString(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.DepartureAirport_4.name()))) {
			FlightInformation  flightInformation  = new FlightInformation();
			
			flightInformation.setCarrierCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.CarrierCode_4.name()));
			flightInformation.setStopOverCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.StopOverCode_4.name()));
			flightInformation.setDepartureAirport(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.DepartureAirport_4.name()));
			flightInformation.setArrivalAirport(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.ArrivalAirport_4.name()));
			flightInformation.setFlightCode(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.FlightCode_4.name()));
			flightInformation.setFareClass(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.FareClass_4.name()));
			flightInformation.setFareBasis(fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.FareBasis_4.name()).replaceAll("[^a-zA-Z0-9]", ""));
			
			credEntryRecord.getEntryAdditionalInformation().getFlightInfos().add(flightInformation);
		}
		
		
	}


	private void populateBatchInfo(CreditBatch creditBatch, String line) throws  NumberFormatException,Exception{
		
		String issuerCity = null; 
		issuerCity = fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.IssuerCity.name());
		
		creditBatch.setIssuerCity(issuerCity);
	}


	private void populateInvoiceHeaderInformation(CreditInvoiceHeader creditInvoiceHeader, String line)throws NumberFormatException , Exception  {

		String merchantAgreementId = null;
		String invoiceName = null ;
		String airlineName = null; 
	
		
		airlineName = fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.AirlineName.name());
		invoiceName =fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.InvoiceName.name());
		merchantAgreementId = fileAttributeController.getAttributeFromrecord(line, EntryRecordAttribute.MerchantAgreementId.name());
		
		creditInvoiceHeader.setAirlineName(airlineName);
		creditInvoiceHeader.setInvoiceName(invoiceName);
		creditInvoiceHeader.setMerchantAgreementId(merchantAgreementId);
		creditInvoiceHeader.setMidLookupCode(merchantAgreementId);
		
	}

}