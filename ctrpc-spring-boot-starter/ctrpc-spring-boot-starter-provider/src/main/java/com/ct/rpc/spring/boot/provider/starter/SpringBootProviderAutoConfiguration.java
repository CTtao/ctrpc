package com.ct.rpc.spring.boot.provider.starter;

import com.ct.rpc.provider.spring.RpcSpringServer;
import com.ct.rpc.spring.boot.provider.config.SpringBootProviderConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@Configuration
@EnableConfigurationProperties
public class SpringBootProviderAutoConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "ctrpc.ct.provider")
    public SpringBootProviderConfig springBootProviderConfig(){
        return new SpringBootProviderConfig();
    }

    @Bean
    public RpcSpringServer rpcSpringServer(final SpringBootProviderConfig springBootProviderConfig){
        return new RpcSpringServer(springBootProviderConfig.getServerAddress(),
                springBootProviderConfig.getRegistryAddress(),
                springBootProviderConfig.getRegistryType(),
                springBootProviderConfig.getRegistryLoadBalanceType(),
                springBootProviderConfig.getReflectType(),
                springBootProviderConfig.getHeartbeatInterval(),
                springBootProviderConfig.getScanNotActiveChannelInterval(),
                springBootProviderConfig.isEnableResultCache(),
                springBootProviderConfig.getResultCacheExpire(),
                springBootProviderConfig.getCorePoolSize(),
                springBootProviderConfig.getMaxPoolSize(),
                springBootProviderConfig.getFlowType(),
                springBootProviderConfig.getMaxConnections(),
                springBootProviderConfig.getDisuseStrategyType(),
                springBootProviderConfig.isEnableBuffer(),
                springBootProviderConfig.getBufferSize());
    }
}
