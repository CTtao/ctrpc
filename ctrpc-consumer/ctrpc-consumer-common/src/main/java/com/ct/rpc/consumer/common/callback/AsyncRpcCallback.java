package com.ct.rpc.consumer.common.callback;

/**
 * @author CT
 * @version 1.0.0
 * @description 异步回调接口
 */
public interface AsyncRpcCallback {
    /**
     * 成功后的回调方法
     */
    void onSuccess(Object result);

    /**
     * 异常的回调方法
     */
    void onException(Exception e);
}
