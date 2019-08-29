package com.techcellance.filehandler.beans;

import java.io.Serializable;

public class EmailInformation implements Serializable{

	private static final long serialVersionUID = 1L;
	private String emailTemplateId = null; 
	private String emailFrom = null ;
	private String emailRecipient = null;
	private String emailPass = null;
	private String emailHost = null; 
	private Integer  emailPort = 25; 
	private String emailSubject = null; 
	private String emailBody  = null ;
	private String emailFooter = null; 
	private boolean isActive = false;
	
	public String getEmailTemplateId() {
		return emailTemplateId;
	}
	public void setEmailTemplateId(String emailTemplateId) {
		this.emailTemplateId = emailTemplateId;
	}
	public String getEmailFrom() {
		return emailFrom;
	}
	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}
	public String getEmailRecipient() {
		return emailRecipient;
	}
	public void setEmailRecipient(String emailRecipient) {
		this.emailRecipient = emailRecipient;
	}
	public String getEmailPass() {
		return emailPass;
	}
	public void setEmailPass(String emailPass) {
		this.emailPass = emailPass;
	}
	public String getEmailHost() {
		return emailHost;
	}
	public void setEmailHost(String emailHost) {
		this.emailHost = emailHost;
	}
	public Integer getEmailPort() {
		return emailPort;
	}
	public void setEmailPort(Integer emailPort) {
		this.emailPort = emailPort;
	}
	public String getEmailSubject() {
		return emailSubject;
	}
	public void setEmailSubject(String emailSubject) {
		this.emailSubject = emailSubject;
	}
	public String getEmailBody() {
		return emailBody;
	}
	public void setEmailBody(String emailBody) {
		this.emailBody = emailBody;
	}
	public String getEmailFooter() {
		return emailFooter;
	}
	public void setEmailFooter(String emailFooter) {
		this.emailFooter = emailFooter;
	}
	public boolean isActive() {
		return isActive;
	}
	public void setActive(boolean isActive) {
		this.isActive = isActive;
	}

}
