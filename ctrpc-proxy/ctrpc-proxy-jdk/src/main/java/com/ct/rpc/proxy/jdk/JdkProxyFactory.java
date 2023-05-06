package com.ct.rpc.proxy.jdk;

import com.ct.rpc.proxy.api.BaseProxyFactory;
import com.ct.rpc.proxy.api.ProxyFactory;

import java.lang.reflect.Proxy;

/**
 * @author CT
 * @version 1.0.0
 * @description jdk动态代理
 */
public class JdkProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {
    public <T> T getProxy(Class<T> clazz){
        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[]{clazz},
                objectProxy
        );
    }
}
