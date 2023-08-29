package com.ct.rpc.ratelimiter.base;

import com.ct.rpc.ratelimiter.api.RateLimiterInvoker;

/**
 * @author CT
 * @version 1.0.0
 * @description 抽象限流器
 */
public abstract class AbstractRateLimiterInvoker implements RateLimiterInvoker {
    /**
     * 在milliSeconds毫秒内最多能够通过的请求个数
     */
    protected int permits;
    /**
     * 毫秒数
     */
    protected int milliSeconds;

    @Override
    public void init(int permits, int milliSeconds) {
        this.permits = permits;
        this.milliSeconds = milliSeconds;
    }
}
