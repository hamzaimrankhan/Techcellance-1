package com.techcellance.filehandler.util;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.techcellance.filehandler.beans.FileConfiguration;


public class FTPUtil {


	private static Logger LGR = LogManager.getLogger(FTPUtil.class);
	
	public static Session getSession(FileConfiguration fileConfiguration) throws JSchException
	{
		
		JSch jsch = new JSch();
        Session session = null;
        	jsch.addIdentity(fileConfiguration.getSftpPassword());
            session = jsch.getSession(fileConfiguration.getSftpUser(),fileConfiguration.getSftpUrl(),fileConfiguration.getSftpPort());
            session.setConfig("StrictHostKeyChecking", "no");
            
            session.connect();		
            
            
        return session;
	}
	
	
	public static void closeSession(Session session) {
		
		if(null != session)
		{

			session.disconnect();
		}
		
		
	}
	
	public static ChannelSftp getSFTPChannel(Session session) throws Exception
	{
		Channel channel = null;
		ChannelSftp sftpChannel = null;
		if(null == session)
		{
			return null ;
		}
	    
		try {
			channel = session.openChannel("sftp");

			channel.connect();
			sftpChannel = (ChannelSftp) channel;
		
		} catch (JSchException e) {
			LGR.warn("##EXCEPTION##", e);
			return null;
		}
		
		
		return sftpChannel;
	}
	
	
	public static void closeSFTPChannel(ChannelSftp  sftpChannel) throws Exception {
		
		if(null != sftpChannel)
		{

			sftpChannel.disconnect();
		}
		
		
	}
	
	
	public static List<String> getListOfFilesFromSftpServer(FileConfiguration fileConfiguration) throws Exception
	{
		
		List<String> files = new ArrayList<String>();
		Session session = null; 
		ChannelSftp channelSftp = null; 
		
		try
		{
		
		session = FTPUtil.getSession(fileConfiguration);
		if(CommonUtils.isNullObject(session)) {
			LGR.info(LGR.isInfoEnabled()? " Could  not get the session  so unable to fetch the list of files" : null );
			return files;
		}
		
		channelSftp = FTPUtil.getSFTPChannel(session);
		if(CommonUtils.isNullObject(channelSftp)) {
			LGR.info(LGR.isInfoEnabled()? " Could  not get the channel sftp  so unable to fetch the list of files" : null );
			return files;
		}
		
		String sourceSftpPath =channelSftp.pwd() + fileConfiguration.getSoucrePath();  
		channelSftp.cd(sourceSftpPath);
		@SuppressWarnings("unchecked")
		Vector<ChannelSftp.LsEntry> list = channelSftp.ls(CommonUtils.getFileNameConvention(fileConfiguration.getFileNameConvention(),fileConfiguration.getnDaysBeforeFile()));
		for(ChannelSftp.LsEntry entry : list) {
			files.add(entry.getFilename());
		}
		if(CommonUtils.isNullOrEmptyCollection(files)) {
		LGR.info(LGR.isInfoEnabled()? "No files found on sftp path("+  sourceSftpPath + ") \n" :null);
		}else {
		LGR.info(LGR.isInfoEnabled()? "Files found on sftp path("+  sourceSftpPath + ") are : \n" + files.toString():null);
		}
		
		}finally{
			FTPUtil.closeSFTPChannel(channelSftp);
			FTPUtil.closeSession(session);
		}
		
		return files;
		
	}
	
	
	public static BufferedReader  readFileFromSftpServer(FileConfiguration fileConfiguration,String fileName,ChannelSftp channelSftp ) throws Exception
	{
		BufferedReader br = null; 
		String sourceSftpPath =  null; 
		
		
	
		if(CommonUtils.isNullObject(channelSftp)) {
			return br; 
			
		}
		sourceSftpPath =channelSftp.pwd()  + fileConfiguration.getSoucrePath() + fileName ;
		LGR.debug(LGR.isDebugEnabled()? "Going to read file from follwoing path on sftp server " + sourceSftpPath: null);
		
		InputStream stream = channelSftp.get( sourceSftpPath);  
		br = new BufferedReader(new InputStreamReader(stream));
           		
		return br ;
	}
	
	
	public static boolean  moveProcessedFileFromSftpServer(FileConfiguration fileConfiguration,String fileName) 
	{		
		String sourceSftpPath =  null; 
		String destSftpPath   = null ;
		Session session = null; 
		ChannelSftp channelSftp = null; 
		boolean isFileMovedToDestFolder = false;
		try
		{ 
			
			session = FTPUtil.getSession(fileConfiguration);
			if(null == session) {
				LGR.info(LGR.isInfoEnabled()? " Could  not get the session  so unable to move file to the destination folder" : null );
				return isFileMovedToDestFolder;
			}
			
			channelSftp = FTPUtil.getSFTPChannel(session);
			if(null == channelSftp) {
				LGR.info(LGR.isInfoEnabled()? " Could  not get the channel sftp  so unable to move file to the destination folder" : null );
				return isFileMovedToDestFolder;
			}	
		
		sourceSftpPath = channelSftp.pwd()  + fileConfiguration.getSoucrePath() + fileName ;
		destSftpPath   = channelSftp.pwd()  + fileConfiguration.getDestinationPath() + fileName;
		
		LGR.debug(LGR.isDebugEnabled()? "Going to move file from follwoing path " + sourceSftpPath + " to : "  + destSftpPath :  null);
		
		channelSftp.rename(sourceSftpPath, destSftpPath);
		
		LGR.debug(LGR.isDebugEnabled()? "Sccessfully moved file from follwoing path " + sourceSftpPath + " to : "  + destSftpPath :  null);
		isFileMovedToDestFolder = true;
		   
		} catch (Exception e) {
                LGR.warn("Exception occurred during moving file from SFTP server due to " + e );
                return isFileMovedToDestFolder ;

       }
		
		return isFileMovedToDestFolder ;
	}
	
}
	
