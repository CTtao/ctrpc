package com.ct.rpc.ratelimiter.counter;

import com.ct.rpc.ratelimiter.base.AbstractRateLimiterInvoker;
import com.ct.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author CT
 * @version 1.0.0
 * @description 根据单位周期内的请求次数限流
 */
@SPIClass
public class CounterRateLimiterInvoker extends AbstractRateLimiterInvoker {
    private final Logger logger = LoggerFactory.getLogger(CounterRateLimiterInvoker.class);
    private final AtomicInteger currentCount = new AtomicInteger(0);
    private volatile long lastTimeStamp = System.currentTimeMillis();
    private final ThreadLocal<Boolean> threadLocal = new ThreadLocal<>();

    @Override
    public boolean tryAcquire() {
        logger.info("execute counter rate limiter...");
        //获取当前时间
        long currentTimeStamp = System.currentTimeMillis();
        //超过单位周期
        if (currentTimeStamp - lastTimeStamp >= milliSeconds){
            lastTimeStamp = currentTimeStamp;
            currentCount.set(0);
            return true;
        }
        //当前请求小于permits
        if (currentCount.incrementAndGet() <= permits){
            threadLocal.set(true);
            return true;
        }
        return false;
    }

    @Override
    public void release() {
        if (threadLocal.get()){
            try {
                currentCount.decrementAndGet();
            }finally {
                threadLocal.remove();
            }
        }
    }
}
