package com.ct.rpc.enhanced.loadbalancer.hash.weight;

import com.ct.rpc.loadbalancer.base.BaseEnhancedServiceLoadBalancer;
import com.ct.rpc.protocol.meta.ServiceMeta;
import com.ct.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPIClass
public class HashWeightServiceEnhancedLoadBalancer extends BaseEnhancedServiceLoadBalancer {
    private final Logger logger = LoggerFactory.getLogger(HashWeightServiceEnhancedLoadBalancer.class);

    @Override
    public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIP) {
        logger.info("基于增强型加权Hash算法的负载均衡策略...");
        servers = this.getWeightServiceMetaList(servers);
        if (servers == null || servers.isEmpty()){
            return null;
        }
        int index = Math.abs(hashCode) % servers.size();
        return servers.get(index);
    }
}
