package com.ct.rpc.loadbalancer.hash.weight;

import com.ct.rpc.loadbalancer.api.ServiceLoadBalancer;
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
public class HashWeightServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {
    private final Logger logger = LoggerFactory.getLogger(HashWeightServiceLoadBalancer.class);

    @Override
    public T select(List<T> servers, int hashCode, String sourceIP) {
        logger.info("基于加权Hash算法的负载均衡策略...");
        if (servers == null || servers.isEmpty()){
            return null;
        }
        hashCode = Math.abs(hashCode);
        int count = hashCode % servers.size();
        if (count <= 0){
            count = servers.size();
        }
        int index = hashCode % count;
        return servers.get(index);
    }
}
