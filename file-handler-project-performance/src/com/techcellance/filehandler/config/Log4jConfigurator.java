package com.techcellance.filehandler.config;


import java.io.File;

import org.apache.log4j.PropertyConfigurator;

import com.techcellance.filehandler.util.Constants;

/**
 * @author Hamza Imran Khan
 * 
 * **/


public class Log4jConfigurator {

	public static void configureLog4J2()
	{
		try
		{
			String log4jConfigFile = System.getProperty("user.dir") + File.separator + Constants.LOG4J2_CONFIG_FILE_NAME;
			System.setProperty("log4j.configurationFile", log4jConfigFile);
			System.setProperty("log4j2.contextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
			PropertyConfigurator.configure(log4jConfigFile);
		}
		catch(Exception e)
		{
		}
	}
	
}