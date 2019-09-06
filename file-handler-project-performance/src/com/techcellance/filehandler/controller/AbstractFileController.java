package com.techcellance.filehandler.controller;

import org.quartz.CronScheduleBuilder;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.techcellance.filehandler.beans.FileConfiguration;
import com.techcellance.filehandler.beans.ResponseInfo;
import com.techcellance.filehandler.controller.impl.BSPFileController;
import com.techcellance.filehandler.controller.impl.BinFileController;
import com.techcellance.filehandler.controller.impl.MidFileController;
import com.techcellance.filehandler.enums.FileFormat;
import com.techcellance.filehandler.jobs.BSPFileJob;
import com.techcellance.filehandler.jobs.BinFileJob;
import com.techcellance.filehandler.jobs.MidFileJob;

public abstract class AbstractFileController {

	protected FileConfiguration fileConfiguration = null;
	
	public FileConfiguration getFileConfiguration() {
		return fileConfiguration;
	}

	public void setFileConfiguration(FileConfiguration fileConfiguration) {
		this.fileConfiguration = fileConfiguration;
	}
	
	public static AbstractFileController getInstance(FileConfiguration fileConfiguration ) throws Exception {
		if(FileFormat.BSP == FileFormat.getFileFormatByFormatId(fileConfiguration.getFileType())){
			return new BSPFileController(fileConfiguration);
		}
		else if (FileFormat.BIN_FILE == FileFormat.getFileFormatByFormatId(fileConfiguration.getFileType())){
			return new BinFileController(fileConfiguration);
		}
		else if(FileFormat.MID_LOOKUP == FileFormat.getFileFormatByFormatId(fileConfiguration.getFileType())){
			return new MidFileController(fileConfiguration);
		}
		
		throw new Exception("File format '" + fileConfiguration.getFileType() + "' not supported!");
	}
	
	public static void createScheduler(FileConfiguration fileConfiguration) throws Exception {
		if(FileFormat.BSP == FileFormat.getFileFormatByFormatId(fileConfiguration.getFileType())) {
			createScheduler(BSPFileJob.class, fileConfiguration);
		}
		else if (FileFormat.BIN_FILE == FileFormat.getFileFormatByFormatId(fileConfiguration.getFileType())){
			createScheduler(BinFileJob.class, fileConfiguration);
		}
		else if(FileFormat.MID_LOOKUP == FileFormat.getFileFormatByFormatId(fileConfiguration.getFileType())){
			createScheduler(MidFileJob.class, fileConfiguration);
		}
	}
	
	public static <T extends Job> void createScheduler(Class<T> type, FileConfiguration fileConfiguration) throws Exception {
		JobDataMap dataMap = new JobDataMap();
	    dataMap.put("AbstractFileController", getInstance(fileConfiguration));
		
		JobDetail job = JobBuilder.newJob(type)
				.withIdentity(type.getSimpleName(), Scheduler.DEFAULT_GROUP)
				.usingJobData(dataMap)
				.build();
		
    	Trigger trigger = TriggerBuilder.newTrigger()
    			.withIdentity(type.getSimpleName() + "Trigger", Scheduler.DEFAULT_GROUP)
				.withSchedule(CronScheduleBuilder.cronSchedule(fileConfiguration.getCronExpression()))
				.build();
    	
    	//0 0/1 1-20 ? * * *
    	Scheduler scheduler = new StdSchedulerFactory().getScheduler();
    	scheduler.start();
    	scheduler.scheduleJob(job, trigger);
	}
	
	public abstract ResponseInfo processFiles();
}