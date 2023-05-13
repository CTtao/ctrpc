package com.ct.rpc.loadbalancer.hash;

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
public class HashServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {
    private final Logger logger = LoggerFactory.getLogger(HashServiceLoadBalancer.class);

    @Override
    public T select(List<T> servers, int hashCode) {
        logger.info("基于Hash算法的负载均衡策略...");
        if (servers == null || servers.isEmpty()){
            return null;
        }
        int index = Math.abs(hashCode) % servers.size();
        return servers.get(index);
    }
}
