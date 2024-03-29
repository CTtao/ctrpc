package com.ct.rpc.fusing.base;

import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.fusing.api.FusingInvoker;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public abstract class AbstractFusingInvoker implements FusingInvoker {
    /**
     * 熔断状态，1：关闭； 2：半开启； 3：开启
     */
    protected final AtomicInteger fusingStatus = new AtomicInteger(RpcConstants.FUSING_STATUS_CLOSED);
    /**
     * 当前调用次数
     */
    protected final AtomicInteger currentCounter = new AtomicInteger(0);
    /**
     * 当前调用失败的次数
     */
    protected final AtomicInteger currentFailureCounter = new AtomicInteger(0);

    /**
     * 熔断时间范围的开始时间点
     */
    protected volatile long lastTimeStamp = System.currentTimeMillis();

    /**
     * 在milliSeconds毫秒内触发熔断操作的上限值
     * 可能是错误个数，也可能是错误率
     */
    protected double totalFailure;

    /**
     * 熔断时间范围的开始时间点
     */
    protected int milliseconds;

    /**
     * 重置数量
     */
    protected void resetCount(){
        currentFailureCounter.set(0);
        currentCounter.set(0);
    }

    @Override
    public void incrementCount() {
        currentCounter.incrementAndGet();
    }

    @Override
    public void incrementFailureCount() {
        currentFailureCounter.incrementAndGet();
    }

    @Override
    public void init(double totalFailure, int milliSeconds) {
        this.totalFailure = totalFailure <= 0 ? RpcConstants.DEFAULT_FUSING_TOTAL_FAILURE : totalFailure;
        this.milliseconds = milliSeconds <= 0 ? RpcConstants.DEFAULT_FUSING_MILLI_SECONDS : milliSeconds;
    }
}
