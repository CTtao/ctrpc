package com.ct.rpc.ratelimiter.guava;

import com.ct.rpc.ratelimiter.base.AbstractRateLimiterInvoker;
import com.ct.rpc.spi.annotation.SPIClass;
import com.google.common.util.concurrent.RateLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPIClass
public class GuavaRateLimiterInvoker extends AbstractRateLimiterInvoker {
    private final Logger logger = LoggerFactory.getLogger(GuavaRateLimiterInvoker.class);
    private RateLimiter rateLimiter;

    @Override
    public void init(int permits, int milliSeconds) {
        super.init(permits, milliSeconds);
        //单位时间转换为秒
        double permitsPerSecond = ((double) permits)/ milliSeconds * 1000;
        this.rateLimiter = RateLimiter.create(permitsPerSecond);
    }

    @Override
    public boolean tryAcquire() {
        logger.info("execute guava rate limiter...");
        return this.rateLimiter.tryAcquire();
    }

    @Override
    public void release() {
        //todo ignore
    }
}
