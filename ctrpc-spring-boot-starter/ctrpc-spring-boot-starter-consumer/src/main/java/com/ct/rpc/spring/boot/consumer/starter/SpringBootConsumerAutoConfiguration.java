package com.ct.rpc.spring.boot.consumer.starter;

import com.ct.rpc.consumer.RpcClient;
import com.ct.rpc.spring.boot.consumer.config.SpringBootConsumerConfig;
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
public class SpringBootConsumerAutoConfiguration {
    @Bean
    @ConfigurationProperties(prefix = "ctrpc.ct.consumer")
    public SpringBootConsumerConfig springBootConsumerConfig(){
        return new SpringBootConsumerConfig();
    }

    @Bean
    public RpcClient rpcClient(final SpringBootConsumerConfig springBootConsumerConfig){
        return new RpcClient(springBootConsumerConfig.getRegistryAddress(),
                springBootConsumerConfig.getRegistryType(),
                springBootConsumerConfig.getLoadBalanceType(),
                springBootConsumerConfig.getProxy(),
                springBootConsumerConfig.getVersion(),
                springBootConsumerConfig.getGroup(),
                springBootConsumerConfig.getSerializationType(),
                springBootConsumerConfig.getTimeout(),
                springBootConsumerConfig.getAsync(),
                springBootConsumerConfig.getOneway(),
                springBootConsumerConfig.getHeartbeatInterval(),
                springBootConsumerConfig.getScanNotActiveChannelInterval(),
                springBootConsumerConfig.getRetryInterval(),
                springBootConsumerConfig.getRetryTimes());
    }
}
