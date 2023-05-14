package com.ct.rpc.enhanced.loadbalancer.sourceip.hash;

import com.ct.rpc.common.utils.StringUtils;
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
public class SourceIPHashWeightServiceEnhancedLoadBalancer extends BaseEnhancedServiceLoadBalancer {
    private final Logger logger = LoggerFactory.getLogger(SourceIPHashWeightServiceEnhancedLoadBalancer.class);

    @Override
    public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIP) {
        logger.info("增强型基于权重的源IP地址Hash的负载均衡策略...");
        servers = this.getWeightServiceMetaList(servers);
        if (servers == null || servers.isEmpty()){
            return null;
        }
        if (StringUtils.isEmpty(sourceIP)){
            return servers.get(0);
        }
        int resultHashCode = Math.abs(sourceIP.hashCode() + hashCode);
        return servers.get(resultHashCode % servers.size());
    }
}
