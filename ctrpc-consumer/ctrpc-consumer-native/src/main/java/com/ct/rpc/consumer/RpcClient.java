package com.ct.rpc.consumer;

import com.ct.rpc.common.exception.RegistryException;
import com.ct.rpc.consumer.common.RpcConsumer;
import com.ct.rpc.proxy.api.ProxyFactory;
import com.ct.rpc.proxy.api.async.IAsyncObjectProxy;
import com.ct.rpc.proxy.api.config.ProxyConfig;
import com.ct.rpc.proxy.api.object.ObjectProxy;
import com.ct.rpc.proxy.jdk.JdkProxyFactory;
import com.ct.rpc.registry.api.RegistryService;
import com.ct.rpc.registry.api.config.RegistryConfig;
import com.ct.rpc.registry.zookeeper.ZookeeperRegistryService;
import com.ct.rpc.spi.loader.ExtensionLoader;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class RpcClient {
    private final Logger logger = LoggerFactory.getLogger(RpcClient.class);

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
     * 代理
     */
    private String proxy;

    /**
     * 是否异步调用
     */
    private boolean async;

    /**
     * 是否单向调用
     */
    private boolean oneway;

    /**
     * 注册服务
     */
    private RegistryService registryService;

    //心跳间隔时间，默认30s
    private int heartbeatInterval;

    //扫描并移除连接时间，默认60s
    private int scanNotActiveChannelInterval;

    public RpcClient(String registryAddress, String registryType ,String registryLoadBalanceType, String proxy, String serviceVersion, String serviceGroup, String serializationType, long timeout, boolean async, boolean oneway, int heartbeatInterval, int scanNotActiveChannelInterval) {
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.serializationType = serializationType;
        this.proxy = proxy;
        this.timeout = timeout;
        this.async = async;
        this.oneway = oneway;
        this.heartbeatInterval = heartbeatInterval;
        this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
        this.registryService = getRegistryService(registryAddress, registryType, registryLoadBalanceType);
    }

    public <T> T create(Class<T> interfaceClass){
        ProxyFactory proxyFactory = ExtensionLoader.getExtension(ProxyFactory.class, proxy);
        proxyFactory.init(new ProxyConfig(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout,registryService, RpcConsumer.getInstance(heartbeatInterval, scanNotActiveChannelInterval), async, oneway));
        return proxyFactory.getProxy(interfaceClass);
    }

    public <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass){
        return new ObjectProxy<T>(interfaceClass, serviceVersion, serviceGroup, serializationType, timeout, registryService, RpcConsumer.getInstance(heartbeatInterval, scanNotActiveChannelInterval), async, oneway);
    }
    public void shutdown(){
        RpcConsumer.getInstance(heartbeatInterval, scanNotActiveChannelInterval).close();
    }

    private RegistryService getRegistryService(String registryAddress, String registryType, String registryLoadBalanceType){
        if (StringUtils.isEmpty(registryType)) {
            throw new IllegalArgumentException("registry type is null");
        }
        RegistryService registryService = ExtensionLoader.getExtension(RegistryService.class, registryType);
        try {
            registryService.init(new RegistryConfig(registryAddress, registryType, registryLoadBalanceType));
        } catch (Exception e){
            logger.error("RpcClient init registry service throws exception:{}",e);
            throw new RegistryException(e.getMessage(), e);
        }
        return registryService;
    }
}
