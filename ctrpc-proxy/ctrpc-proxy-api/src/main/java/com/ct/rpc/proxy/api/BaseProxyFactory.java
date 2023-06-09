package com.ct.rpc.proxy.api;

import com.ct.rpc.proxy.api.config.ProxyConfig;
import com.ct.rpc.proxy.api.object.ObjectProxy;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public abstract class BaseProxyFactory<T> implements ProxyFactory{
    protected ObjectProxy<T> objectProxy;

    @Override
    public <T> void init(ProxyConfig<T> proxyConfig) {
        this.objectProxy = new ObjectProxy(proxyConfig.getClazz(),
                proxyConfig.getServiceVersion(),
                proxyConfig.getServiceGroup(),
                proxyConfig.getSerializationType(),
                proxyConfig.getTimeout(),
                proxyConfig.getRegistryService(),
                proxyConfig.getConsumer(),
                proxyConfig.isAsync(),
                proxyConfig.isOneway());
    }
}
