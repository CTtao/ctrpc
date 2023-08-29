package com.ct.rpc.ratelimiter.api;

import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.spi.annotation.SPI;

/**
 * @author CT
 * @version 1.0.0
 * @description 限流调用器SPI，秒级单位限流
 */
@SPI(RpcConstants.DEFAULT_RATELIMITER_INVOKER)
public interface RateLimiterInvoker {

    /**
     * 限流方法
     */
    boolean tryAcquire();

    /**
     * 释放资源
     */
    void release();

    /**
     * 在milliSeconds毫秒内最多允许通过的permits个请求
     * @param permits 单位周期内最多允许通过的permits个请求数
     * @param milliSeconds 单位周期
     */
    default void init(int permits, int milliSeconds){}
}
