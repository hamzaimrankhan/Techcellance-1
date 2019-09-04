package com.techcellance.filehandler.main;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techcellance.filehandler.bl.ServiceExecutionHandler;
import com.techcellance.filehandler.config.Log4jConfigurator;
import com.techcellance.filehandler.config.StartupComoFileConfiguration;

public class ServiceMain {

	static
	{
		Log4jConfigurator.configureLog4J2();
	}
	
	private static Logger LGR = LogManager.getLogger(ServiceMain.class);
	
	public static void main(String[] args)
	{
		try
		{
			
			LGR.info(LGR.isInfoEnabled()? "COMO File Handler Service :: Revision Date : 04-09-2019 07:49": null );	
			LGR.info(LGR.isInfoEnabled()?"Going to load congiurations from configuration file for startup and initialization":null);
			
			if(!StartupComoFileConfiguration.loadConfigurations()){
				LGR.info(LGR.isInfoEnabled()? "Could not load configurations from congfig.ini sucessfully" :null);
				System.exit(-1);		
			}	
			
			LGR.info(LGR.isInfoEnabled()? "All Configurations from congfig.ini loaded sucessfully" :null);
			LGR.info("Going to execute the file handler service...");
			ServiceExecutionHandler.execute();
			LGR.info(LGR.isInfoEnabled()? "Service execution handler completed the task successfully" :null);
		}
		catch(Exception e)
		{
			LGR.error("Exception occurred in ServiceMain.main() in file handler service", e);
			//genemail
			System.exit(-1);
		}finally {
			
			System.exit(-1);
		}
	}
	
	
}



