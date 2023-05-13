package com.ct.rpc.loadbalancer.sourceip.hash.weight;

import com.ct.rpc.common.utils.StringUtils;
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
public class SourceIPHashWeightServiceLoadBalancer<T> implements ServiceLoadBalancer<T> {
    private final Logger logger = LoggerFactory.getLogger(SourceIPHashWeightServiceLoadBalancer.class);

    @Override
    public T select(List<T> servers, int hashCode, String sourceIP) {
        logger.info("基于源IP地址加权Hash算法的负载均衡策略...");
        if (servers == null || servers.isEmpty()){
            return null;
        }
        if (StringUtils.isEmpty(sourceIP)){
            return servers.get(0);
        }
        int count = Math.abs(hashCode) % servers.size();
        if (count == 0){
            count = servers.size();
        }
        int resultHashCode = Math.abs(sourceIP.hashCode() + hashCode);
        return servers.get(resultHashCode % count);
    }
}
