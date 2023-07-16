package com.ct.rpc.threadpool;

import com.ct.rpc.constants.RpcConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

/**
 * @author CT
 * @version 1.0.0
 * @description 线程池
 */
public class ConcurrentThreadPool {
    private final Logger logger = LoggerFactory.getLogger(ConcurrentThreadPool.class);

    private ThreadPoolExecutor threadPoolExecutor;

    private static volatile ConcurrentThreadPool instance;

    private ConcurrentThreadPool(){

    }

    private ConcurrentThreadPool(int corePoolSize, int maxPoolSize){
        if (corePoolSize <= 0){
            corePoolSize = RpcConstants.DEFAULT_CORE_POOL_SIZE;
        }
        if (maxPoolSize <= 0){
            maxPoolSize = RpcConstants.DEFAULT_MAXI_NUM_POOL_SIZE;
        }
        if (corePoolSize > maxPoolSize){
            maxPoolSize = corePoolSize;
        }
        this.threadPoolExecutor = new ThreadPoolExecutor(corePoolSize, maxPoolSize,
                RpcConstants.DEFAULT_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(RpcConstants.DEFAULT_QUEUE_CAPACITY));
    }

    /**
     * 单例传递参数创建对象，只以第一次传递的参数为准
     */
    public static ConcurrentThreadPool getInstance(int corePoolSize, int maxPoolSize){
        if (instance == null){
            synchronized (ConcurrentThreadPool.class){
                if (instance == null){
                    instance = new ConcurrentThreadPool(corePoolSize, maxPoolSize);
                }
            }
        }
        return instance;
    }

    public void submit(Runnable task){
        threadPoolExecutor.submit(task);
    }

    public <T> T submit(Callable<T> task){
        Future<T> future = threadPoolExecutor.submit(task);
        if (future == null){
            return null;
        }
        try {
            return future.get();
        } catch (Exception e){
            logger.error("submit callable task exception:{}", e.getMessage());
        }
        return null;
    }

    public void stop(){
        threadPoolExecutor.shutdown();
    }
}
