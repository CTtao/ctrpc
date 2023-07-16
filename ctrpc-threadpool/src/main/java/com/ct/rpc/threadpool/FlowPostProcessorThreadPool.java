package com.ct.rpc.threadpool;

import com.ct.rpc.constants.RpcConstants;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author CT
 * @version 1.0.0
 * @description 流量后置处理器线程池
 */
public class FlowPostProcessorThreadPool {
    private static ThreadPoolExecutor threadPoolExecutor;
    static {
        threadPoolExecutor = new ThreadPoolExecutor(8,8,
                RpcConstants.DEFAULT_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(RpcConstants.DEFAULT_QUEUE_CAPACITY));
    }

    public static void submit(Runnable task){
        threadPoolExecutor.submit(task);
    }

    public static void shutdown(){
        threadPoolExecutor.shutdown();
    }
}
