package com.techcellance.filehandler.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techcellance.filehandler.beans.FileConfiguration;
import com.techcellance.filehandler.dao.AbstractFileHandlerServiceDao;
import com.techcellance.filehandler.dao.impl.FileHandlerServiceDaoImpl;
import com.techcellance.filehandler.util.CommonUtils;
import com.techcellance.filehandler.util.Constants;

public class StartupComoFileConfiguration {

	private static Logger LGR = LogManager.getLogger(StartupComoFileConfiguration.class);
	
	
	
	public static  void populateWorldPayConfigurations() throws SQLException, Exception
	{
		FileHandlerServiceDaoImpl fileHandlerServiceDaoImpl= AbstractFileHandlerServiceDao.getInstance();
		
		fileHandlerServiceDaoImpl.populateWorldPayConfiguration();
	}
	

   public static  void populateComoFileParams() throws SQLException, Exception
	{
		FileHandlerServiceDaoImpl fileHandlerServiceDaoImpl= AbstractFileHandlerServiceDao.getInstance();
		
		fileHandlerServiceDaoImpl.populateComoFileParams();
	}

	public static List<FileConfiguration> fetchFileHandlerConfigurations() throws Exception
	{
		FileHandlerServiceDaoImpl fileHandlerServiceDaoImpl= AbstractFileHandlerServiceDao.getInstance();
		List<FileConfiguration> fileConfigurations = null;
		
 		
		fileConfigurations =  fileHandlerServiceDaoImpl.fetchComoFileConfigurations();

		
		return fileConfigurations;
	}

	public static boolean   loadConfigurations() {
			
		Properties props = new Properties();
	
		String configFilePath = System.getProperty("user.dir") + File.separator + Constants.CONFIG_INI_FILE_PATH;
		LGR.debug(LGR.isDebugEnabled() ? "Going to pick the configuration file from path : " + configFilePath:null);
		
		
		try (FileInputStream in = new FileInputStream(configFilePath)) {

			props.load(in);
		    return  populateProperties(props); 
		    
		} catch (FileNotFoundException fe) {
			LGR.warn("##FileNotFoundExceptiopn## in loadConfiguration" , fe );
			return false;
		} catch (IOException ioe) {
			LGR.warn("#IO#Exceptiopn## in loadConfiguration" , ioe );
			return false  ;
			
		}catch(Exception ex) {
			LGR.warn("##Exceptiopn## in loadConfiguration" , ex );
			return false;
		}
	}

	private static boolean  populateProperties(Properties props) {
		
		Object paramvalue = null;
		if(null == props){
			LGR.info(LGR.isInfoEnabled() ? "Could not load properties as properties file is null or empty ": null);
			return false  ;
		}
		
		paramvalue = props.get(Constants.PARAM_ID_DATABASE_CLASS) ;
		if(null != paramvalue)
		{
			Constants.DATA_BASE_CLASS_NAME  = (String)paramvalue; 
			LGR.debug(LGR.isDebugEnabled() ? "Param id database_class = " + paramvalue :null );
		
		}
		else {
			LGR.debug("Could not load the configuration parameter: " +  Constants.PARAM_ID_DATABASE_CLASS );
			return false ;	
		}
		
		paramvalue = props.get(Constants.PARAM_ID_DATABASE_URL) ;
		if(null != paramvalue)
		{
			Constants.DATA_BASE_URL  = (String)paramvalue; 
			LGR.debug(LGR.isDebugEnabled() ? "Param id database_url = " + paramvalue :null );
			
		}
		else {
			LGR.debug("Could not load the configuration parameter: " +  Constants.PARAM_ID_DATABASE_URL );
			return false ;	
		}
		
		paramvalue = props.get(Constants.PARAM_ID_DATABASE_USER) ;
		if(null != paramvalue)
		{
			Constants.DATA_BASE_USER  = (String)paramvalue; 
			LGR.debug(LGR.isDebugEnabled() ? "Param id database_user = " + paramvalue :null );
			
		}
		else {
			LGR.debug("Could not load the configuration parameter: " +  Constants.PARAM_ID_DATABASE_USER );
			return false ;	
		}
		
		paramvalue = props.get(Constants.PARAM_ID_DATABASE_PASS) ;
		if(null != paramvalue)
		{
			Constants.DATA_BASE_PASS  = (String)paramvalue; 
			LGR.debug(LGR.isDebugEnabled() ? "Param id database_pass = " + "*********" :null );
			
		}
		else {
			LGR.debug("Could not load the configuration parameter: " +  Constants.PARAM_ID_DATABASE_PASS );
			return false ;	
		}
		
		paramvalue = props.get(Constants.PARAM_ID_TEMP_FILE_PATH) ;
		if(null != paramvalue)
		{
			Constants.TEMP_FILE_PATH  = (String)paramvalue; 
			LGR.debug(LGR.isDebugEnabled() ? "Param id temp_file_path  = " + paramvalue :null );
			
		}
		else {
			LGR.debug("Could not load the configuration parameter: " +  Constants.PARAM_ID_TEMP_FILE_PATH );
				
		}
		paramvalue = props.get(Constants.PARAM_ID_MERCHANTCODE) ;
		if(null != paramvalue)
		{
			Constants.MERCHANT_CODE  = (String)paramvalue; 
			LGR.debug(LGR.isDebugEnabled() ? "Param id merchant code  = " + paramvalue :null );
			
		}
		else {
			LGR.debug("Could not load the configuration parameter: " +  Constants.PARAM_ID_MERCHANTCODE );
				
		}
		
		
//		paramvalue = props.get(Constants.PARAM_ID_WORLD_PAY_URL) ;
//		if(null != paramvalue)
//		{
//			Constants.WORLD_PAY_URL  = (String)paramvalue; 
//			LGR.debug(LGR.isDebugEnabled() ? "Param id world_pay_url  = " + paramvalue :null );
//			
//		}
//		else {
//			LGR.debug("Could not load the configuration parameter: " +  Constants.PARAM_ID_WORLD_PAY_URL );
//			return false ;	
//		}
//		
//		paramvalue = props.get(Constants.PARAM_ID_WORLD_PAY_USER) ;
//		if(null != paramvalue)
//		{
//			Constants.WORLD_PAY_USER  = (String)paramvalue; 
//			LGR.debug(LGR.isDebugEnabled() ? "Param id world_pay_user  = " + paramvalue :null );
//			
//		}
//		else {
//			LGR.debug("Could not load the configuration parameter: " +  Constants.PARAM_ID_WORLD_PAY_USER );
//			return false ;	
//		}
//		
//		paramvalue = props.get(Constants.PARAM_ID_WORLD_PAY_PASS) ;
//		if(null != paramvalue)
//		{
//			Constants.WORLD_PAY_PASS  = (String)paramvalue; 
//			LGR.debug(LGR.isDebugEnabled() ? "Param id world_pay_password  = " + "**********" :null );
//			
//		}
//		else {
//			LGR.debug("Could not load the configuration parameter: " +  Constants.PARAM_ID_WORLD_PAY_PASS );
//			return false ;	
//		}
		
		return true;
		
	}


	public  static void init() {
	
		try {

		LGR.info(LGR.isInfoEnabled()?" Going to load world pay configuration and como file parameters" : null);
			populateWorldPayConfigurations();

			populateComoFileParams();
		LGR.info(LGR.isInfoEnabled()?"End of loading world pay configuration and como file parameters" : null);

		
		} catch (SQLException e) {

			CommonUtils.logSqlException(LGR, e);

		} catch (Exception ex) {

			LGR.error("#SQLEXCPETION### in the init method: ", ex);

		}
	
	}
	
	
	
}
