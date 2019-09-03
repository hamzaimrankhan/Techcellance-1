package com.techcellance.filehandler.bl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.techcellance.filehandler.beans.CreditBatchEntryRecord;
import com.techcellance.filehandler.beans.FlightInformation;
import com.techcellance.filehandler.dao.AbstractFileHandlerServiceDao;
import com.techcellance.filehandler.enums.EntryRecordAttribute;
import com.techcellance.filehandler.enums.ResponseCode;
import com.techcellance.filehandler.util.CommonUtils;
import com.techcellance.filehandler.util.Constants;
import com.techcellance.filehandler.util.DefaultTagValues;


public class PaymentServiceProcessor {
	
	private static Logger LGR = LogManager.getLogger(PaymentServiceProcessor.class);
	
	private static PaymentServiceProcessor sHandler = null; 
	
	public static PaymentServiceProcessor  getInstance() {
		
		if(null == sHandler) {
			
			sHandler = new PaymentServiceProcessor();
		}
		
		return sHandler; 	
	}
	
	private  String captureTransaction(String transactionRequest) throws IOException,Exception
	{
		
		String response = null;
		
			
			URL url = new URL(Constants.WORLD_PAY_URL);
			URLConnection conn = url.openConnection();
			conn.setDoOutput(true);
		
			String usernameColonPassword = Constants.WORLD_PAY_USER+":"+Constants.WORLD_PAY_PASS;
			String basicAuthPayload = "Basic " + Base64.getEncoder().encodeToString(usernameColonPassword.getBytes());

		
			conn.setRequestProperty ("Authorization",basicAuthPayload);

			OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());

			writer.write(transactionRequest);
			writer.flush();
			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
			response  = reader.lines().collect(Collectors.joining());
	
			writer.close();
			reader.close();

		
		
		return response;
	}

	private   String generateXMLRequestForDebitTransaction(CreditBatchEntryRecord entry) throws ParserConfigurationException, TransformerException {
			
		 DocumentBuilderFactory fac = null;
		 fac = DocumentBuilderFactory.newInstance();
			
			fac.setNamespaceAware(false);
			fac.setValidating(false);
			fac.setFeature("http://xml.org/sax/features/namespaces", false);
			fac.setFeature("http://xml.org/sax/features/validation", false);
			fac.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			fac.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		 
         DocumentBuilder documentBuilder = fac.newDocumentBuilder();
         Attr attr = null;
         
         Document document = documentBuilder.newDocument();
         DOMImplementation domImpl = document.getImplementation();
         //DOCTYPE
         DocumentType docType = domImpl.createDocumentType("paymentService", "-//Worldpay/DTD Worldpay PaymentService v1//EN", "http://dtd.worldpay.com/paymentService_v1.dtd");
         
         //Payment service Tag
         Element paymentService = document.createElement("paymentService");
         document.appendChild(paymentService);
         attr = document.createAttribute("version");
         attr.setValue("1.4");  // version value
         paymentService.setAttributeNode(attr);
         attr = document.createAttribute("merchantCode"); //merchant code 
         attr.setValue(Constants.MERCHANT_CODE);
         paymentService.setAttributeNode(attr);
         
         Element submit = document.createElement("submit");
         paymentService.appendChild(submit); //submit tag has no attribute
         Element order=  document.createElement("order");
         	
         attr = document.createAttribute("orderCode");
         attr.setValue(!CommonUtils.isNullOrEmptyString(entry.getDocumentNumber())?entry.getDocumentNumber():DefaultTagValues.DOCUMENT_NUMBER);
         order.setAttributeNode(attr);
         submit.appendChild(order);
         
         Element description  = document.createElement("description");
         description.appendChild(document.createTextNode("COMO CAPTURE ONLY"));
         order.appendChild(description);
        
         Element amount=  document.createElement("amount");
         
         if(!CommonUtils.isNullOrEmptyString(entry.getAmmount())){
        	 attr = document.createAttribute("value");
        	 attr.setValue(entry.getAmmount());
        	 amount.setAttributeNode(attr);
         }
         if(!CommonUtils.isNullOrEmptyString(entry.getCurrency())){    
        	 attr = document.createAttribute("currencyCode");
        	 attr.setValue(entry.getCurrency());
        	 amount.setAttributeNode(attr);
         }
         attr = document.createAttribute("exponent");  
         attr.setValue("2");
         amount.setAttributeNode(attr);
         order.appendChild(amount);
         //end of amount
         
         //Start of payment details
         Element paymentDetails=  document.createElement("paymentDetails");
         order.appendChild(paymentDetails);
         
         Element cardSsl = document.createElement("CARD-SSL");
         paymentDetails.appendChild(cardSsl);
         
         Element cardNumber = document.createElement("cardNumber");
         cardSsl.appendChild(cardNumber);
         cardNumber.appendChild(document.createTextNode(entry.getCardNumber()));
         Element expiryDate = document.createElement("expiryDate");
         cardSsl.appendChild(expiryDate);
         Element date = document.createElement("date");
         expiryDate.appendChild(date);
         
         if(!CommonUtils.isNullOrEmptyString(entry.getExpiry())) {
        	 
         attr = document.createAttribute("month");
         attr.setValue(entry.getExpiry().substring(0,2));
         date.setAttributeNode(attr);
         
         attr = document.createAttribute("year");
         attr.setValue( "20"+ entry.getExpiry().substring(2,4));
         date.setAttributeNode(attr);
         }
         Element cardHolderName = document.createElement("cardHolderName");
         cardSsl.appendChild(cardHolderName);
         cardHolderName.appendChild(document.createTextNode(!CommonUtils.isNullOrEmptyString(entry.getPassenger())?entry.getPassenger():DefaultTagValues.PASSENGER_NAME));
         
         if(!CommonUtils.isNullOrEmptyString(entry.getApprovalCode())) {
         Element externalAuhorisationCode = document.createElement("existingAuthorisationCode");
         externalAuhorisationCode.appendChild(document.createTextNode(entry.getApprovalCode()));
         paymentDetails.appendChild(externalAuhorisationCode);
         }
         
		Element routingMID = document.createElement("routingMID");
		if (!CommonUtils.isNullOrEmptyString(entry.getMidLookUpCode())) {
			routingMID.appendChild(document.createTextNode(entry.getMidLookUpCode()));
		} else {
			routingMID.appendChild(document.createTextNode("DUMMY"));
		}
		paymentDetails.appendChild(routingMID);
   
         Element branchSpecificExtension = document.createElement("branchSpecificExtension");
         order.appendChild(branchSpecificExtension);
         Element airline = document.createElement("airline");
         branchSpecificExtension.appendChild(airline);
       
         
         attr = document.createAttribute("code");
         attr.setValue(!CommonUtils.isNullOrEmptyString(entry.getAirLineCode())?entry.getAirLineCode():DefaultTagValues.AIRLINE_CODE);
         airline.setAttributeNode(attr);
       
         Element airLineName = document.createElement("airlineName");
         airline.appendChild(airLineName);
         airLineName.appendChild(document.createTextNode(!CommonUtils.isNullOrEmptyString(entry.getAirlineName())?entry.getAirlineName():DefaultTagValues.AIRLINE_NAME));
         
         Element passenger = document.createElement("passenger");
         airline.appendChild(passenger);
         passenger.appendChild(document.createTextNode(entry.getPassenger()));
         
         attr = document.createAttribute("code");
         attr.setValue(!CommonUtils.isNullOrEmptyString(entry.getPassengerCode())?entry.getPassengerCode():DefaultTagValues.PASSENGER_CODE);
         passenger.setAttributeNode(attr);
         
         Element ticket = document.createElement("ticket");
         airline.appendChild(ticket);
         
         if(!CommonUtils.isNullOrEmptyString(entry.getTicketCode())){
             
         attr = document.createAttribute("code");
         attr.setValue(entry.getTicketCode());
         ticket.setAttributeNode(attr);
         
         attr = document.createAttribute("restricted");
         attr.setValue(!CommonUtils.isNullOrEmptyString(entry.getTicketRestricted())?entry.getTicketRestricted():"0");
         ticket.setAttributeNode(attr);
         
         }
         Element issuer = document.createElement("issuer");
         ticket.appendChild(issuer);
         Element address = document.createElement("address");
         issuer.appendChild(address);
         
         Element address1 = document.createElement("address1");
         address.appendChild(address1);
         address1.appendChild(document.createTextNode(!CommonUtils.isNullOrEmptyString(entry.getLegalEntityAddress())? entry.getLegalEntityAddress(): DefaultTagValues.ISSUER_ADDRESS1));
         
         Element postalCode = document.createElement("postalCode");
         address.appendChild(postalCode);
         postalCode.appendChild(document.createTextNode(!CommonUtils.isNullOrEmptyString(entry.getLegalPostalCode())?entry.getLegalPostalCode():DefaultTagValues.POSTAL_CODE));
      
         Element city = document.createElement("city");
         address.appendChild(city);
         city.appendChild(document.createTextNode(!CommonUtils.isNullOrEmptyString(entry.getLegalEntityCity())?entry.getLegalEntityCity():DefaultTagValues.ISSUER_CITY));
         
         Element countryCode = document.createElement("countryCode");
         address.appendChild(countryCode);
         countryCode.appendChild(document.createTextNode(!CommonUtils.isNullOrEmptyString(entry.getLegalCountryCode())? entry.getLegalCountryCode():DefaultTagValues.COUNTRY_CODE));
        
         populateFlightInformation(document,ticket,entry);
         
        Element agent = document.createElement("agent");
        airline.appendChild(agent);
        
        attr = document.createAttribute("code");
	    attr.setValue(!CommonUtils.isNullOrEmptyString(entry.getAgentCode())?entry.getAgentCode():DefaultTagValues.AGENT_CODE);
	    agent.setAttributeNode(attr);
        agent.appendChild(document.createTextNode(!CommonUtils.isNullOrEmptyString(entry.getInvoiceName())?entry.getInvoiceName():DefaultTagValues.AGENT_NAME));
        
        
        
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, docType.getPublicId());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
        StringWriter writer = new StringWriter();
        DOMSource domSource = new DOMSource(document);
		transformer.transform(domSource, new StreamResult(writer));
		String xmlString = writer.getBuffer().toString();  
        
		return xmlString;
		
	}

	private void populateFlightInformation(Document document, Element ticket, CreditBatchEntryRecord credEntryRecord) {
		Attr attr = null;
		int index = 0;
		for (FlightInformation flightInformation : credEntryRecord.getEntryAdditionalInformation().getFlightInfos()) {

			Element flight = document.createElement("flight");
			ticket.appendChild(flight);

			attr = document.createAttribute("carrierCode");
			attr.setValue(!CommonUtils.isNullOrEmptyString(flightInformation.getCarrierCode())?flightInformation.getCarrierCode():DefaultTagValues.CARRIER_CODE);
			flight.setAttributeNode(attr);

			attr = document.createAttribute("flightCode");
			attr.setValue(!CommonUtils.isNullOrEmptyString(flightInformation.getFlightcode())?flightInformation.getFlightcode():DefaultTagValues.FIGHT_CODE);
			flight.setAttributeNode(attr);

			Element departureAirport = document.createElement("departureAirport");
			departureAirport.appendChild(document.createTextNode(!CommonUtils.isNullOrEmptyString(flightInformation.getDepartureAirport())?flightInformation.getDepartureAirport():DefaultTagValues.DEPARTURE_AIRPORT));
			flight.appendChild(departureAirport);

			Element arrivalAirport = document.createElement("arrivalAirport");
			arrivalAirport.appendChild(document.createTextNode(!CommonUtils.isNullOrEmptyString(flightInformation.getArrivalAirport())?flightInformation.getArrivalAirport():DefaultTagValues.ARRIVAL_AIRPORT));
			flight.appendChild(arrivalAirport);

			Element departureDate = document.createElement("departureDate");
			Element date2 = document.createElement("date");
			flight.appendChild(departureDate);
			departureDate.appendChild(date2);
			
			if(CommonUtils.isNullOrEmptyString(credEntryRecord.getDepartureDate())){
				CommonUtils.populateCurrentDepartureDate(credEntryRecord);
			}
			attr = document.createAttribute("dayOfMonth");
			attr.setValue(credEntryRecord.getDepartureDate());	
			date2.setAttributeNode(attr);
			attr = document.createAttribute("month");
			attr.setValue(credEntryRecord.getDepartureMonth());
			date2.setAttributeNode(attr);
			attr = document.createAttribute("year");
			attr.setValue(credEntryRecord.getDepartureYear());
			date2.setAttributeNode(attr);
		
			Element farebase = document.createElement("fare");
			flight.appendChild(farebase);
			attr = document.createAttribute("class");
			attr.setValue(!CommonUtils.isNullOrEmptyString(flightInformation.getFareClass())?flightInformation.getFareClass():DefaultTagValues.FARE_CLASS);
			farebase.setAttributeNode(attr);
			attr = document.createAttribute("basis");
			attr.setValue(!CommonUtils.isNullOrEmptyString(flightInformation.getFareBasis())?flightInformation.getFareBasis():DefaultTagValues.FARE_BASIS);
			farebase.setAttributeNode(attr);

			Element tax = document.createElement("tax");
			flight.appendChild(tax);
			Element amount2 = document.createElement("amount");
			tax.appendChild(amount2);
			attr = document.createAttribute("value");
			attr.setValue((index++ == 0)  ?  Integer.toString(credEntryRecord.getEntryAdditionalInformation().getTaxAmount() ): "0");
			amount2.setAttributeNode(attr);	
			attr = document.createAttribute("currencyCode");
			attr.setValue(credEntryRecord.getCurrency());
			amount2.setAttributeNode(attr);
			attr = document.createAttribute("exponent");
			attr.setValue("2");
			amount2.setAttributeNode(attr);
		}
	}

	private   void populateCaptureResponseForTransaction(String response, CreditBatchEntryRecord entry, AtomicInteger successFulRecordCount, AtomicInteger failedRecordCount)throws ParserConfigurationException , SAXException , IOException {
		DocumentBuilder builder;
		DocumentBuilderFactory fac = null;

			fac = DocumentBuilderFactory.newInstance();
		
			fac.setNamespaceAware(false);
			fac.setValidating(false);
			fac.setFeature("http://xml.org/sax/features/namespaces", false);
			fac.setFeature("http://xml.org/sax/features/validation", false);
			fac.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			fac.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			
		builder =fac.newDocumentBuilder();
		
		InputSource src = new InputSource();
		src.setCharacterStream(new StringReader(response));
		NodeList xmlNode = null;
		Document doc = builder.parse(src);
		
		if(null == doc) {
			
			LGR.info(LGR.isInfoEnabled()?"Could not convert and read from the XML response recieved from web service" :null );
			return;
		}

		if(Constants.ORPHAN_TRANSACTION.equalsIgnoreCase(entry.getTransactionType())) {
			
			entry.setResponseCode(ResponseCode.SUCCESS.getRespCode());
			entry.setResponseDescription( ((!CommonUtils.isNullOrEmptyCollection(entry.getMissingMandatoryInfos())
					? String.join(",", entry.getMissingMandatoryInfos())
							: null )+ " is missing"));
			successFulRecordCount.incrementAndGet();
			
			return;
			
		}else if(Constants.CREDIT_TRANSACTION.equalsIgnoreCase(entry.getTransactionType())) {		
			
			xmlNode = doc.getElementsByTagName("ok");
			if(!CommonUtils.isNullObject(xmlNode.item(0))){
				
				entry.setResponseCode(ResponseCode.SUCCESS.getRespCode());
				entry.setResponseDescription(ResponseCode.SUCCESS.getRespDesc());
				successFulRecordCount.incrementAndGet();
				return ;
			}	
				
		}
		else if (Constants.DEBIT_TRANSACTION.equalsIgnoreCase(entry.getTransactionType())) {
			
				xmlNode = doc.getElementsByTagName("AuthorisationId");
				if (!CommonUtils.isNullObject(xmlNode.item(0))) {
					String id = xmlNode.item(0).getAttributes().getNamedItem("id").getTextContent();
					entry.setAuthorizationId(id);
				}

				xmlNode = doc.getElementsByTagName("cardNumber");
				if (!CommonUtils.isNullObject(xmlNode.item(0))) {
					entry.setMaskedCardNumber(xmlNode.item(0).getTextContent());

					entry.setResponseCode(ResponseCode.SUCCESS.getRespCode());
					entry.setResponseDescription(ResponseCode.SUCCESS.getRespDesc());
					successFulRecordCount.incrementAndGet();
					return;

				}

				
		}
		
		xmlNode = doc.getElementsByTagName("error");
		if(!CommonUtils.isNullObject(xmlNode.item(0)))
		{
			String errorCode= xmlNode.item(0).getAttributes().getNamedItem("code").getTextContent();
			entry.setResponseCode(errorCode);
			failedRecordCount.getAndIncrement();
			String cdata  = xmlNode.item(0).getTextContent();
			entry.setResponseDescription(cdata);
			return; 
		}
		
		
		entry.setResponseCode(ResponseCode.NO_ERROR_AND_SUCCESS_TAG_IN_RESPONSE.getRespCode());
		entry.setResponseDescription(ResponseCode.NO_ERROR_AND_SUCCESS_TAG_IN_RESPONSE.getRespDesc());
		failedRecordCount.incrementAndGet();
		
		
	}
	
 	private String generateXMLRequestForCreditTransaction(CreditBatchEntryRecord entry) throws ParserConfigurationException, TransformerException {
 		 DocumentBuilderFactory fac = null;
		 fac = DocumentBuilderFactory.newInstance();
			
			fac.setNamespaceAware(false);
			fac.setValidating(false);
			fac.setFeature("http://xml.org/sax/features/namespaces", false);
			fac.setFeature("http://xml.org/sax/features/validation", false);
			fac.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			fac.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
		 
         DocumentBuilder documentBuilder = fac.newDocumentBuilder();
         Attr attr = null;
         
         Document document = documentBuilder.newDocument();
         DOMImplementation domImpl = document.getImplementation();
         //DOCTYPE
         DocumentType docType = domImpl.createDocumentType("paymentService", "-//Worldpay/DTD Worldpay PaymentService v1//EN", "http://dtd.worldpay.com/paymentService_v1.dtd");
         
         //Payment service Tag
         Element paymentService = document.createElement("paymentService");
         document.appendChild(paymentService);
         attr = document.createAttribute("version");
         attr.setValue("1.4");  // version value
         paymentService.setAttributeNode(attr);
         attr = document.createAttribute("merchantCode"); //merchant code 
         attr.setValue(Constants.MERCHANT_CODE);
         paymentService.setAttributeNode(attr);
         
         Element submit = document.createElement("submit");
         paymentService.appendChild(submit); //submit tag has no attribute
         Element order=  document.createElement("order");
         attr = document.createAttribute("orderCode");
         attr.setValue((!CommonUtils.isNullOrEmptyString(entry.getDocumentNumber())?entry.getDocumentNumber():DefaultTagValues.DOCUMENT_NUMBER));
         order.setAttributeNode(attr);
         
         submit.appendChild(order);
         
         Element description  = document.createElement("description");
         description.appendChild(document.createTextNode("COMO CAPTURE ONLY"));
         order.appendChild(description);
        
         Element amount=  document.createElement("amount");
         
         if(!CommonUtils.isNullOrEmptyString(entry.getAmmount())){
        	 attr = document.createAttribute("value");
        	 attr.setValue(entry.getAmmount());
        	 amount.setAttributeNode(attr);
         }
         if(!CommonUtils.isNullOrEmptyString(entry.getCurrency())){    
        	 attr = document.createAttribute("currencyCode");
        	 attr.setValue(entry.getCurrency());
        	 amount.setAttributeNode(attr);
         }
         attr = document.createAttribute("exponent");  
         attr.setValue("2");
         amount.setAttributeNode(attr);
         order.appendChild(amount);
         //end of amount
         
         //Start of payment details
         Element paymentDetails=  document.createElement("paymentDetails");
         order.appendChild(paymentDetails);
         
         attr = document.createAttribute("action");
         attr.setValue(Constants.REFUND_ACTION_ACTION);
         paymentDetails.setAttributeNode(attr);
         
         Element cardSsl = document.createElement("CARD-SSL");
         paymentDetails.appendChild(cardSsl);
         
         Element cardNumber = document.createElement("cardNumber");
         cardSsl.appendChild(cardNumber);
         cardNumber.appendChild(document.createTextNode(entry.getCardNumber()));
         Element expiryDate = document.createElement("expiryDate");
         cardSsl.appendChild(expiryDate);
         Element date = document.createElement("date");
         expiryDate.appendChild(date);
      
         attr = document.createAttribute("month");
         attr.setValue((!CommonUtils.isNullOrEmptyString(entry.getExpiry()) && entry.getExpiry().length() ==4)?entry.getExpiry().substring(0,2):DefaultTagValues.EXPIRY_MONTH);
         date.setAttributeNode(attr);
        
         attr = document.createAttribute("year");
         attr.setValue((!CommonUtils.isNullOrEmptyString(entry.getExpiry()) && entry.getExpiry().length() ==4)? "20"+ entry.getExpiry().substring(2,4):DefaultTagValues.EXPIRY_YEAR);
         date.setAttributeNode(attr);
        
         Element cardHolderName = document.createElement("cardHolderName");
         cardSsl.appendChild(cardHolderName);
         cardHolderName.appendChild(document.createTextNode(entry.getPassenger()));
           
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, docType.getPublicId());
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
        StringWriter writer = new StringWriter();
        DOMSource domSource = new DOMSource(document);
		transformer.transform(domSource, new StreamResult(writer));
		String xmlString = writer.getBuffer().toString();  
        
		return xmlString;
	
	}
 	
	private String generateXMLRequestForOrpahnTransaction(CreditBatchEntryRecord entry)
			throws ParserConfigurationException, TransformerException {
		DocumentBuilderFactory fac = null;
		fac = DocumentBuilderFactory.newInstance();

		fac.setNamespaceAware(false);
		fac.setValidating(false);
		fac.setFeature("http://xml.org/sax/features/namespaces", false);
		fac.setFeature("http://xml.org/sax/features/validation", false);
		fac.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
		fac.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

		DocumentBuilder documentBuilder = fac.newDocumentBuilder();
		Attr attr = null;

		Document document = documentBuilder.newDocument();
		DOMImplementation domImpl = document.getImplementation();
		// DOCTYPE
		DocumentType docType = domImpl.createDocumentType("paymentService",
				"-//Worldpay/DTD Worldpay PaymentService v1//EN", "http://dtd.worldpay.com/paymentService_v1.dtd");

		// Payment service Tag
		Element paymentService = document.createElement("paymentService");
		document.appendChild(paymentService);
		attr = document.createAttribute("version");
		attr.setValue("1.4"); // version value
		paymentService.setAttributeNode(attr);
		attr = document.createAttribute("merchantCode"); // merchant code
		attr.setValue(Constants.MERCHANT_CODE);
		paymentService.setAttributeNode(attr);

		Element submit = document.createElement("submit");
		paymentService.appendChild(submit); // submit tag has no attribute
		Element order = document.createElement("order");
		attr = document.createAttribute("orderCode");
		attr.setValue(!CommonUtils.isNullOrEmptyString(entry.getDocumentNumber()) ? entry.getDocumentNumber()
				: DefaultTagValues.DOCUMENT_NUMBER);
		order.setAttributeNode(attr);

		submit.appendChild(order);

		Element description = document.createElement("description");
		String descp = (!CommonUtils.isNullOrEmptyCollection(entry.getMissingMandatoryInfos())
				? String.join(",", entry.getMissingMandatoryInfos())
				: null )+ " is missing";
		description.appendChild(
				document.createTextNode(descp));
		order.appendChild(description);

		Element amount = document.createElement("amount");

		attr = document.createAttribute("value");
		attr.setValue(
				!CommonUtils.isNullOrEmptyString(entry.getAmmount()) ? entry.getAmmount() : DefaultTagValues.AMOUNT);
		amount.setAttributeNode(attr);
		attr = document.createAttribute("currencyCode");
		attr.setValue(!CommonUtils.isNullOrEmptyString(entry.getCurrency()) ? entry.getCurrency()
				: DefaultTagValues.CURRENCY_CODE);
		amount.setAttributeNode(attr);
		attr = document.createAttribute("exponent");
		attr.setValue("2");
		amount.setAttributeNode(attr);
		order.appendChild(amount);
		// end of amount

		Element paymentMethodMask = document.createElement("paymentMethodMask");
		order.appendChild(paymentMethodMask);
		Element include = document.createElement("include");
		paymentMethodMask.appendChild(include);
		attr = document.createAttribute("code");
		include.setAttributeNode(attr);
		attr.setValue("ALL");

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, docType.getPublicId());
		transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, docType.getSystemId());
		StringWriter writer = new StringWriter();
		DOMSource domSource = new DOMSource(document);
		transformer.transform(domSource, new StreamResult(writer));
		String xmlString = writer.getBuffer().toString();

		return xmlString;

	}
 	
	public void processCreditBatchEntryRecord(CreditBatchEntryRecord entry, AtomicInteger successFulRecordCount,AtomicInteger failedRecordCount ) {
	
		try {
		
		boolean isAgentCodePopulated = false;	
		if(Constants.DEBIT_TRANSACTION.equalsIgnoreCase(entry.getTransactionType()) || Constants.CREDIT_TRANSACTION.equalsIgnoreCase(entry.getTransactionType())) {
	
		AbstractFileHandlerServiceDao dao = AbstractFileHandlerServiceDao.getInstance(); 
		validateRecordMandatoryInfo(entry);
		if(Constants.ORPHAN_TRANSACTION.equalsIgnoreCase(entry.getTransactionType())) {
			entry.setStatus(Constants.SUCCESSFUL_STATUS);
		}
		else{
			isAgentCodePopulated = dao.populateAgentCodeInformation(entry);				
		}
		
		if(isAgentCodePopulated || Constants.ORPHAN_TRANSACTION.equalsIgnoreCase(entry.getTransactionType())){
			
		LGR.info(LGR.isInfoEnabled()? "Going to generate XML request for entry with document number: " +  entry.getDocumentNumber() +  " and card number:  "  + CommonUtils.getMaskedCardNumber(entry.getCardNumber()):null);
		String xmlRequest =generateXMLRequest(entry);
		LGR.debug(LGR.isDebugEnabled()? "XML request generated for entry record with document number: " +  entry.getDocumentNumber() +  " and card number:  "  + CommonUtils.getMaskedCardNumber(entry.getCardNumber()) + "is \n" + xmlRequest  :null);
				
		LGR.info(LGR.isInfoEnabled() ? "Going to capture transaction for entry with document number:  " + entry.getDocumentNumber()+  " and card number:  "  + CommonUtils.getMaskedCardNumber(entry.getCardNumber()) : null);
		String response = PaymentServiceProcessor.getInstance().captureTransaction(xmlRequest);
		LGR.debug(LGR.isDebugEnabled()? "XML response recieved for entry record with document number: " +  entry.getDocumentNumber() +  " and card number:  "  + CommonUtils.getMaskedCardNumber(entry.getCardNumber()) + "is \n" + response  :null);
		
		if(null != response){
			PaymentServiceProcessor.getInstance().populateCaptureResponseForTransaction(response,entry,successFulRecordCount,failedRecordCount );
		}else{
			LGR.debug(LGR.isDebugEnabled() ? "Response recieved for entry with docuemt number is : "  + entry.getDocumentNumber() +  " and card number:  "  + CommonUtils.getMaskedCardNumber(entry.getCardNumber()) + " please refer logs for detials" :null);
		}
	
		
		}else{
			
			LGR.info(LGR.isInfoEnabled()?"MID Agent code not found in the data repository for order number : " + entry.getDocumentNumber() +  " and card number:  "  + CommonUtils.getMaskedCardNumber(entry.getCardNumber()) : null);
		}
		
		}else {
			
			entry.setResponseCode(ResponseCode.ILLEGAL_TRANSACTION_TYPE.getRespCode());
			entry.setResponseDescription(ResponseCode.ILLEGAL_TRANSACTION_TYPE.getRespDesc());
			failedRecordCount.incrementAndGet();
		}
		
		
		}catch(Exception ex) {
			
			LGR.error("Exception in processCreditBatchEntryRecord: " ,ex );
			entry.setResponseCode(ResponseCode.EXCEPTION .getRespCode());
			entry.setResponseDescription("Exception : "+ex.getMessage());
			failedRecordCount.incrementAndGet();
		}
	
	}

	private String generateXMLRequest(CreditBatchEntryRecord entry) throws ParserConfigurationException, TransformerException {
		 
		String xmlRequest = null; 
		if(Constants.DEBIT_TRANSACTION.equals(entry.getTransactionType()) ){
			xmlRequest = PaymentServiceProcessor.getInstance().generateXMLRequestForDebitTransaction(entry);
		}else if(Constants.CREDIT_TRANSACTION.equals(entry.getTransactionType())){
			xmlRequest =  PaymentServiceProcessor.getInstance().generateXMLRequestForCreditTransaction(entry);
		}else if (Constants.ORPHAN_TRANSACTION.equals(entry.getTransactionType())){
			xmlRequest =  PaymentServiceProcessor.getInstance().generateXMLRequestForOrpahnTransaction(entry);  //here for orphan transaction
		}
		return xmlRequest;
	}

	private void validateRecordMandatoryInfo(CreditBatchEntryRecord entry) throws Exception{
		
		if(!CommonUtils.isNullObject(entry)){
			if(CommonUtils.isNullOrEmptyString(entry.getCardNumber())){
				entry.getMissingMandatoryInfos().add(EntryRecordAttribute.CardNumber.name());
			}
			if(Constants.DEBIT_TRANSACTION.equalsIgnoreCase(entry.getTransactionType()) && CommonUtils.isNullOrEmptyString(entry.getExpiry())){
				entry.getMissingMandatoryInfos().add(EntryRecordAttribute.Expiry.name());		
			}if(Constants.DEBIT_TRANSACTION.equalsIgnoreCase(entry.getTransactionType()) &&  CommonUtils.isNullOrEmptyString(entry.getApprovalCode())){
				entry.getMissingMandatoryInfos().add(EntryRecordAttribute.ApprovalCode.name());	
			}if(!CommonUtils.isNullOrEmptyCollection(entry.getMissingMandatoryInfos())){
				entry.setTransactionType(Constants.ORPHAN_TRANSACTION);
			}
			
		}else{
			LGR.warn(LGR.isWarnEnabled() ? "Entry record is null or empty in the method validateRecordMandatoryInfo " :null);
		}
	}
 	
}
