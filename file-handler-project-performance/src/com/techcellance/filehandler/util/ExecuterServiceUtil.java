package com.techcellance.filehandler.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ExecuterServiceUtil {


	private static Logger LGR = LogManager.getLogger(ExecuterServiceUtil.class);
	
	public static ExecutorService initFixedSizeExecutorService(int size)
	{
		return Executors.newFixedThreadPool(size);
	}
	
	public static void waitForExecutorServiceCompletion(ExecutorService executorService, int sleepSeconds)
	{
		try
		{
			executorService.awaitTermination(sleepSeconds, TimeUnit.SECONDS);
		}
		catch(InterruptedException e )
		{
			LGR.warn("Interupted Termination", e);
		}
	}
	
	public static void shutdownExecutorService(ExecutorService executorService)
	{
		if(executorService != null)
		{
			executorService.shutdown();
		}
	}
}