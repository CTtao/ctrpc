package com.ct.rpc.proxy.api.async;

import com.ct.rpc.proxy.api.future.RpcFuture;

/**
 * @author CT
 * @version 1.0.0
 * @description 动态代理异步接口
 */
public interface IAsyncObjectProxy {

    /**
     * 异步代理对象调用方法
     * @param funcName 方法名称
     * @param args 参数
     * @return 封装好的Rpc对象
     */
    RpcFuture call(String funcName, Object... args);
}
