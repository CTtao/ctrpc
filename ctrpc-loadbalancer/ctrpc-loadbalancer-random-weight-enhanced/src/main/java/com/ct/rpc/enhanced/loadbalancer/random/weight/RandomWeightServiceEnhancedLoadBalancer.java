package com.ct.rpc.enhanced.loadbalancer.random.weight;

import com.ct.rpc.loadbalancer.base.BaseEnhancedServiceLoadBalancer;
import com.ct.rpc.protocol.meta.ServiceMeta;
import com.ct.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPIClass
public class RandomWeightServiceEnhancedLoadBalancer extends BaseEnhancedServiceLoadBalancer {
    private final Logger logger = LoggerFactory.getLogger(RandomWeightServiceEnhancedLoadBalancer.class);

    @Override
    public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIP) {
        logger.info("基于加强型加权随机算法的负载均衡策略...");
        servers = this.getWeightServiceMetaList(servers);
        if (servers == null || servers.isEmpty()){
            return null;
        }
        Random random = new Random();
        int index = random.nextInt(servers.size());
        return servers.get(index);
    }
}
