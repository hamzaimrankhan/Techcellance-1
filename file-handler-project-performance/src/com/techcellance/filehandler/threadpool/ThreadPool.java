package com.techcellance.filehandler.threadpool;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.techcellance.filehandler.beans.ResponseInfo;

public final class ThreadPool {
	private static Logger LGR = LogManager.getLogger(ThreadPool.class);
	
	private  ExecutorService threadPool = null;

    public ThreadPool() {
    
    }
    
    public synchronized   void initializePool(int threadPoolSize) {
        if (threadPool != null) {
            return;
        }
        synchronized (ThreadPool.class) {
            threadPool = Executors.newFixedThreadPool(threadPoolSize);
        }
    }

    public  ExecutorService getThreadPool(){
        return threadPool;
    }

    public  Future<ResponseInfo> processTask(Callable<ResponseInfo> task) {
        return threadPool.submit(task);
    }

    public  Future<Boolean> processSimpleResponseTask(Callable<Boolean> task) {
        return threadPool.submit(task);
    }

    public  void processTask(Runnable task) {
        LGR.info(LGR.isInfoEnabled() ? "Executing provided task by allocating a thread from pool... " : null);
        threadPool.execute(task);
    }

    public  void shutDownPool() {
        threadPool.shutdown();
    }

}
