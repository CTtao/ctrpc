package com.ct.rpc.ratelimiter.semaphore;

import com.ct.rpc.ratelimiter.base.AbstractRateLimiterInvoker;
import com.ct.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

/**
 * @author CT
 * @version 1.0.0
 * @description 基于Semaphore的限流策略
 */
@SPIClass
public class SemaphoreRateLimiterInvoker extends AbstractRateLimiterInvoker {
    private final Logger logger = LoggerFactory.getLogger(SemaphoreRateLimiterInvoker.class);
    private Semaphore semaphore;

    @Override
    public void init(int permits, int milliSeconds) {
        super.init(permits, milliSeconds);
        this.semaphore = new Semaphore(permits);
    }

    @Override
    public boolean tryAcquire() {
        logger.info("execute semaphore rate limiter...");
        return semaphore.tryAcquire();
    }

    @Override
    public void release() {
        semaphore.release();
    }
}
