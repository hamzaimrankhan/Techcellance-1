package com.techcellance.filehandler.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techcellance.filehandler.beans.ResponseInfo;

public final  class BinEntryThreadPool {

	private static Logger LGR = LogManager.getLogger(CreditEntryThreadPool.class);
	
	private static ExecutorService threadPool = null;

    private  BinEntryThreadPool() {
    
    }
    
    public synchronized  static void initializePool(int threadPoolSize) {
        if (threadPool != null) {
            return;
        }
        synchronized (CreditEntryThreadPool.class) {
            threadPool = Executors.newFixedThreadPool(threadPoolSize);
        }
    }

    public static ExecutorService getThreadPool(){
        return threadPool;
    }

    public static Future<ResponseInfo> processTask(Callable<ResponseInfo> task) {
        return threadPool.submit(task);
    }

    public static Future<Boolean> processSimpleResponseTask(Callable<Boolean> task) {
        return threadPool.submit(task);
    }

    public static void processTask(Runnable task) {
        LGR.info(LGR.isInfoEnabled() ? "Executing provided task by allocating a thread from pool... " : null);
        threadPool.execute(task);
    }

    public static void shutDownPool() {
        threadPool.shutdown();
    }
	
}
