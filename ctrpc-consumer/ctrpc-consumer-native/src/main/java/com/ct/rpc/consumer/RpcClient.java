package com.ct.rpc.consumer;

import com.ct.rpc.consumer.common.RpcConsumer;
import com.ct.rpc.proxy.jdk.JdkProxyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class RpcClient {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcClient.class);

    /**
     * 服务版本
     */
    private String serviceVersion;
    /**
     * 服务分组
     */
    private String serviceGroup;
    /**
     * 序列化类型
     */
    private String serializationType;
    /**
     * 超时时间
     */
    private long timeout;

    /**
     * 是否异步调用
     */
    private boolean async;

    /**
     * 是否单向调用
     */
    private boolean oneway;

    public RpcClient(String serviceVersion, String serviceGroup, String serializationType, long timeout, boolean async, boolean oneway) {
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.serializationType = serializationType;
        this.timeout = timeout;
        this.async = async;
        this.oneway = oneway;
    }

    public <T> T create(Class<T> interfaceClass){
        JdkProxyFactory<T> jdkProxyFactory = new JdkProxyFactory<>(serviceVersion, serviceGroup, timeout, RpcConsumer.getInstance(), serializationType, async, oneway);
        return jdkProxyFactory.getProxy(interfaceClass);
    }

    public void shutdown(){
        RpcConsumer.getInstance().close();
    }
}
