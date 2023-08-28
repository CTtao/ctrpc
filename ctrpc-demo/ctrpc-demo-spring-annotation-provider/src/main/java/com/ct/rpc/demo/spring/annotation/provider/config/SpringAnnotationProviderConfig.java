package com.ct.rpc.demo.spring.annotation.provider.config;

import com.ct.rpc.provider.spring.RpcSpringServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@Configuration
@ComponentScan(value = {"com.ct.rpc.demo"})
@PropertySource(value = {"classpath:rpc.properties"})
public class SpringAnnotationProviderConfig {
    @Value("${registry.address}")
    private String registryAddress;

    @Value("${registry.type}")
    private String registryType;

    @Value("${registry.loadBalance.type}")
    private String registryLoadBalanceType;

    @Value("${server.address}")
    private String serverAddress;

    @Value("${reflect.type}")
    private String reflectType;

    @Value("${server.heartbeatInterval}")
    private int heartbeatInterval;

    @Value("${server.scanNotActiveChannelInterval}")
    private int scanNotActiveChannelInterval;

    /**
     * 是否开启结果缓存
     */
    @Value("${server.enableResultCache}")
    private boolean enableResultCache;

    /**
     * 结果缓存的时长
     */
    @Value("${server.resultCacheExpire}")
    private int resultCacheExpire;

    @Value("${server.corePoolSize}")
    private int corePoolSize;

    @Value("${server.maxPoolSize}")
    private int maxPoolSize;

    @Value("${server.flowType}")
    private String flowType;

    @Value("${server.maxConnections}")
    private int maxConnections;

    @Value("${server.disuseStrategyType}")
    private String disuseStrategyType;

    @Value("server.enableBuffer")
    private boolean enableBuffer;

    @Value("server.bufferSize")
    private int bufferSize;

    @Bean
    public RpcSpringServer rpcSpringServer(){
        return new RpcSpringServer(serverAddress, registryAddress, registryType, registryLoadBalanceType, reflectType,
                heartbeatInterval, scanNotActiveChannelInterval,
                enableResultCache, resultCacheExpire,
                corePoolSize, maxPoolSize,
                flowType,
                maxConnections, disuseStrategyType,
                enableBuffer, bufferSize);
    }

}
