package com.techcellance.filehandler.bl;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techcellance.filehandler.beans.FileConfiguration;
import com.techcellance.filehandler.beans.ResponseInfo;
import com.techcellance.filehandler.config.StartupComoFileConfiguration;
import com.techcellance.filehandler.controller.AbstractFileController;
import com.techcellance.filehandler.util.CommonUtils;

public class ServiceExecutionHandler {

	private static Logger LGR = LogManager.getLogger(ServiceExecutionHandler.class);
	
	
	public static void execute() {

		List<FileConfiguration> fileConfigurations = null;
		ResponseInfo responseInfo = new ResponseInfo();
		try {

			StartupComoFileConfiguration.init();
			LGR.debug(LGR.isDebugEnabled() ? "Going to load file configurations to read settlement files " : null);
			fileConfigurations = StartupComoFileConfiguration.fetchFileHandlerConfigurations();
			
			
			if (CommonUtils.isNullOrEmptyCollection(fileConfigurations)) {

				LGR.info(LGR.isInfoEnabled() ? "No configuration found in database to settle the settlement files": null);
				return;
			}

			for (FileConfiguration fileConfiguration : fileConfigurations) {

				AbstractFileController fileController = AbstractFileController.getInstance(fileConfiguration);
				fileController.processFiles();
				LGR.info(LGR.isInfoEnabled()?" Response recieved from file type("+ fileConfiguration.getFileType()+") is response code : " + responseInfo.getRespCode()  + " ,response description " +  responseInfo.getRespDesc() :null);

			}

		} catch (Exception ex) {
			LGR.error("##EXCEPTION## in Service Execution Handler " , ex);
			//genemail
		}

	}
}
