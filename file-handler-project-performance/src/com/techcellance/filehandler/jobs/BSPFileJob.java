package com.techcellance.filehandler.jobs;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.techcellance.filehandler.beans.ResponseInfo;
import com.techcellance.filehandler.bl.ServiceExecutionHandler;
import com.techcellance.filehandler.controller.AbstractFileController;

@DisallowConcurrentExecution
public class BSPFileJob implements Job {

	private static Logger LGR = LogManager.getLogger(ServiceExecutionHandler.class);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap jobDataMap = context.getMergedJobDataMap();  
		AbstractFileController fileController = (AbstractFileController) jobDataMap.get("AbstractFileController");

		LGR.info("...BSP File Job Execution Started...");
		
		ResponseInfo responseInfo = fileController.processFiles();
		LGR.info(LGR.isInfoEnabled() ? " Response recieved from file type(" + fileController.getFileConfiguration().getFileType() + ") is response code : " + responseInfo.getRespCode()  + " , response description " +  responseInfo.getRespDesc() : null);
		
		LGR.info("...BSP File Job Execution Ended...");
	}
}