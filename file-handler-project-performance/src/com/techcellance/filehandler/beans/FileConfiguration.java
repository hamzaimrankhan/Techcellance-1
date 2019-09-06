package com.techcellance.filehandler.beans;

import java.io.Serializable;
import java.util.Date;

public class FileConfiguration implements Serializable {

	
	private static final long serialVersionUID = 1L;
	
	private int serialNo = 0;
	private int fileType = 0;
	private String fileConfigurationDescription  = null;
	private String sftpUrl  = null;
	private int sftpPort  = 0 ;
	private String sftpUser = null ; 
	private String sftpPassword = null;
	private String soucrePath = null; 
	private String destinationPath = null;
	private Date   scheduledDateTme = null;
	private String description = null;
	private String fileNameConvention =null;
	private String cronExpression=null;
	private int nDaysBeforeFile = 0 ; 
 	
	public int getSerialNo() {
		return serialNo;
	}
	public void setSerialNo(int serialNo) {
		this.serialNo = serialNo;
	}
	public int getFileType() {
		return fileType;
	}
	public void setFileType(int fileType) {
		this.fileType = fileType;
	}
	public String getSftpUrl() {
		return sftpUrl;
	}
	public void setSftpUrl(String sftpUrl) {
		this.sftpUrl = sftpUrl;
	}
	public int getSftpPort() {
		return sftpPort;
	}
	public void setSftpPort(int sftpPort) {
		this.sftpPort = sftpPort;
	}
	public String getSftpUser() {
		return sftpUser;
	}
	public void setSftpUser(String sftpUser) {
		this.sftpUser = sftpUser;
	}
	public String getSftpPassword() {
		return sftpPassword;
	}
	public void setSftpPassword(String sftpPassword) {
		this.sftpPassword = sftpPassword;
	}
	public String getSoucrePath() {
		return soucrePath;
	}
	public void setSoucrePath(String soucrePath) {
		this.soucrePath = soucrePath;
	}
	public String getDestinationPath() {
		return destinationPath;
	}
	public void setDestinationPath(String destinationPath) {
		this.destinationPath = destinationPath;
	}
	public Date getScheduledDateTme() {
		return scheduledDateTme;
	}
	public void setScheduledDateTme(Date scheduledDateTme) {
		this.scheduledDateTme = scheduledDateTme;
	}
	
	public String getFileConfigurationDescription() {
		return fileConfigurationDescription;
	}
	public void setFileConfigurationDescription(String fileConfigurationDescription) {
		this.fileConfigurationDescription = fileConfigurationDescription;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	@Override
	public String toString() {
		return "FileConfiguration [serialNo=" + serialNo + ", fileType=" + fileType + ", fileConfigurationDescription="
				+ fileConfigurationDescription + ", sftpUrl=" + sftpUrl + ", sftpPort=" + sftpPort + ", sftpUser="
				+ sftpUser + ", soucrePath=" + soucrePath + ", destinationPath=" + destinationPath
				+ ", scheduledDateTme=" + scheduledDateTme + ", description=" + description + "]";
	}
	public String getFileNameConvention() {
		return fileNameConvention;
	}
	public void setFileNameConvention(String fileNameConvention) {
		this.fileNameConvention = fileNameConvention;
	}
	public String getCronExpression() {
		return cronExpression;
	}
	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}
	public int getnDaysBeforeFile() {
		return nDaysBeforeFile;
	}
	public void setnDaysBeforeFile(int nDaysBeforeFile) {
		this.nDaysBeforeFile = nDaysBeforeFile;
	}

}
