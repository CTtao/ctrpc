package com.ct.rpc.loadbalancer.random.weight;

import com.ct.rpc.loadbalancer.api.ServiceLoadBalancer;
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
public class RandomWeightServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {
    private final Logger logger = LoggerFactory.getLogger(RandomWeightServiceLoadBalancer.class);

    @Override
    public T select(List<T> servers, int hashCode, String sourceIP) {
        logger.info("基于加权随机算法的负载均衡策略...");
        if (servers == null || servers.isEmpty()){
            return null;
        }
        hashCode = Math.abs(hashCode);
        int count = hashCode % servers.size();
        if (count <= 1){
            count = servers.size();
        }
        Random random = new Random();
        int index = random.nextInt(count);
        return servers.get(index);
    }
}
