package com.ct.rpc.enhanced.loadbalancer.robin.weight;

import com.ct.rpc.loadbalancer.base.BaseEnhancedServiceLoadBalancer;
import com.ct.rpc.protocol.meta.ServiceMeta;
import com.ct.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPIClass
public class RobinWeightServiceEnhancedLoadBalancer extends BaseEnhancedServiceLoadBalancer {
    private final Logger logger = LoggerFactory.getLogger(RobinWeightServiceEnhancedLoadBalancer.class);

    private volatile AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIP) {
        logger.info("基于增强型加权轮询算法的负载均衡策略...");
        servers = this.getWeightServiceMetaList(servers);
        if (servers == null || servers.isEmpty()){
            return null;
        }
        int index = atomicInteger.incrementAndGet();
        if (index >= Integer.MAX_VALUE - 10000){
            atomicInteger.set(0);
        }
        return servers.get(index % servers.size());
    }
}
