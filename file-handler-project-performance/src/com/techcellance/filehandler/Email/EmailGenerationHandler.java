package com.techcellance.filehandler.Email;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techcellance.filehandler.beans.CreditBatchEntryRecord;
import com.techcellance.filehandler.beans.EmailInformation;
import com.techcellance.filehandler.beans.FailedEntryInfo;
import com.techcellance.filehandler.dao.AbstractFileHandlerServiceDao;
import com.techcellance.filehandler.util.CommonUtils;
import com.techcellance.filehandler.util.Constants;


public class EmailGenerationHandler {

	private static Logger LGR = LogManager.getLogger(EmailGenerationHandler.class);
	public EmailGenerationHandler(){
		
	}

	public static void generateEmailForFailedRecords(String templateId, List<FailedEntryInfo> failedEntryInfos , String fileName){
		EmailInformation emailInformation = AbstractFileHandlerServiceDao.getInstance().fetchEmailConfiguration(templateId);
		generateEmailContent(failedEntryInfos,fileName,emailInformation);	
		if(!CommonUtils.isNullObject(emailInformation) && !CommonUtils.isNullOrEmptyString(emailInformation.getEmailFrom()) && !CommonUtils.isNullOrEmptyString(emailInformation.getEmailRecipient()) ){
			sendEmail(emailInformation);
		}else{
			
			LGR.warn("Couldnot generate email for failed record due to no configuration or in-active status");
		}
	}
	
	public static void generateEmailForMissingMIDInformation(String templateId, List<CreditBatchEntryRecord> records,String fileName){	
		
		EmailInformation emailInformation = AbstractFileHandlerServiceDao.getInstance().fetchEmailConfiguration(templateId);
		generateEmailContentForMissingMidAgent(records,fileName,emailInformation);	
		if(!CommonUtils.isNullObject(emailInformation) && !CommonUtils.isNullOrEmptyString(emailInformation.getEmailFrom()) && !CommonUtils.isNullOrEmptyString(emailInformation.getEmailRecipient()) ){
			sendEmail(emailInformation);
		}else{
			
			LGR.warn("Couldnot generate email for failed record due to no configuration or in-active status");
		}
	}	
	
	private static void generateEmailContentForMissingMidAgent(List<CreditBatchEntryRecord> records, String fileName,EmailInformation emailInformation) {		
		StringBuilder  messageBody = new StringBuilder();
		messageBody.append(emailInformation.getEmailBody());
		messageBody.append(Constants.NextLine);
		messageBody.append("Total Error Records :" +records.size());
		messageBody.append(Constants.NextLine);
		messageBody.append("Record Number of failed records:"  +CommonUtils.getDocumentNumbner(records) );
		messageBody.append(Constants.NextLine);
		messageBody.append(emailInformation.getEmailFooter());
		emailInformation.setEmailBody(messageBody.toString());
		emailInformation.setEmailSubject(emailInformation.getEmailSubject().concat("["+ fileName +"]"));		
	}
	
	public static void generateEmailForSystemException(String templateId,Exception ex){
		
		EmailInformation emailInformation = AbstractFileHandlerServiceDao.getInstance().fetchEmailConfiguration(templateId);
		generateEmailContent(ex, emailInformation);	
		if(!CommonUtils.isNullObject(emailInformation) && !CommonUtils.isNullOrEmptyString(emailInformation.getEmailFrom()) && !CommonUtils.isNullOrEmptyString(emailInformation.getEmailRecipient())){
			sendEmail(emailInformation);
		}else{
			LGR.warn("Couldnot generate email for failed record due to no configuration or in-active status");
		}
	}
	
	private static void generateEmailContent(List<FailedEntryInfo> failedEntryInfos,String fileName, EmailInformation emailInformation) {		
		StringBuilder  messageBody = new StringBuilder();
		messageBody.append(emailInformation.getEmailBody());
		messageBody.append(Constants.NextLine);
		messageBody.append("Total Error Records :" +failedEntryInfos.size());
		messageBody.append(Constants.NextLine);
		messageBody.append("Record Number of failed records:"  +CommonUtils.getRecNumbers(failedEntryInfos)  );
		messageBody.append(Constants.NextLine);
		messageBody.append(emailInformation.getEmailFooter());
		emailInformation.setEmailBody(messageBody.toString());
		emailInformation.setEmailSubject(emailInformation.getEmailSubject().concat("["+ fileName +"]"));
		
	}	
	
	private static void generateEmailContent(Exception ex, EmailInformation emailInformation) {		
		StringBuilder  messageBody = new StringBuilder();
		messageBody.append(emailInformation.getEmailBody());
		messageBody.append(Constants.NextLine);
		messageBody.append(ex);
		messageBody.append(Constants.NextLine);
		messageBody.append(emailInformation.getEmailFooter());
		emailInformation.setEmailBody(messageBody.toString());
	}	
	
	private static void sendEmail(EmailInformation emailInformation ) {

		try {

			Properties properties = System.getProperties();
			properties.setProperty("mail.smtp.host", emailInformation.getEmailHost());
			properties.setProperty("mail.smtp.port", emailInformation.getEmailPort().toString());
			properties.setProperty("mail.smtp.auth", "true");
			javax.mail.Authenticator auth = new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(emailInformation.getEmailFrom().trim(),emailInformation.getEmailPass().trim());}
			};
			Session session = Session.getInstance(properties,auth);
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress(emailInformation.getEmailFrom()));
			message.addRecipients(Message.RecipientType.TO,getAllReciepients(emailInformation.getEmailRecipient()));
			message.setSubject(emailInformation.getEmailSubject());
			message.setText(emailInformation.getEmailBody());

			Transport.send(message);

		} catch (AddressException e) {
			LGR.error("Exception in sendEmail", e);

		} catch (MessagingException e) {
			LGR.error("Exception in sendEmail", e);
		} catch (Exception ex) {
			LGR.error("Exception in sendEmail", ex);

		}

	}

	private static InternetAddress[] getAllReciepients(String emailAddresses) throws Exception{
		
		List<String> emails  = CommonUtils.convertStringToCommaSeperatedList(emailAddresses);
		InternetAddress[] addresses =  new InternetAddress[emails.size()] ; 
		for (int i=0;i<emails.size();i++) {
			addresses[i]= new InternetAddress(emails.get(i));	
		}		
		return addresses;
		
	}
	
	
	
}
