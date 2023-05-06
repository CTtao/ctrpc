package com.ct.rpc.proxy.api;

import com.ct.rpc.proxy.api.config.ProxyConfig;

/**
 * @author CT
 * @version 1.0.0
 * @description 动态代理工厂类
 */
public interface ProxyFactory {

    /**
     * 获取代理对象
     */
    <T> T getProxy(Class<T> clazz);

    /**
     * 默认初始化方法
     */
    default <T> void init(ProxyConfig<T> proxyConfig){}
}
