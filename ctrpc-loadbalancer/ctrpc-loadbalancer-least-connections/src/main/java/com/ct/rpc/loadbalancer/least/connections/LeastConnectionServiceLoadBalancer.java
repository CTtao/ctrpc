package com.ct.rpc.loadbalancer.least.connections;

import com.ct.rpc.loadbalancer.api.ServiceLoadBalancer;
import com.ct.rpc.loadbalancer.context.ConnectionsContext;
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
public class LeastConnectionServiceLoadBalancer implements ServiceLoadBalancer<ServiceMeta> {
    private final Logger logger = LoggerFactory.getLogger(LeastConnectionServiceLoadBalancer.class);

    @Override
    public ServiceMeta select(List<ServiceMeta> servers, int hashCode, String sourceIP) {
        logger.info("基于最少连接数的负载均衡策略...");
        if (servers == null || servers.isEmpty()){
            return null;
        }
        ServiceMeta serviceMeta = this.getNullServiceMeta(servers);
        if (serviceMeta == null){
            serviceMeta = this.getServiceMeta(servers);
        }
        return serviceMeta;
    }

    private ServiceMeta getServiceMeta(List<ServiceMeta> servers){
        ServiceMeta serviceMeta = servers.get(0);
        Integer serviceMetaCount = ConnectionsContext.getValue(serviceMeta);
        for (int i = 0; i < servers.size(); i++) {
            ServiceMeta meta = servers.get(i);
            Integer metaCount = ConnectionsContext.getValue(meta);
            if (serviceMetaCount > metaCount){
                serviceMetaCount = metaCount;
                serviceMeta = meta;
            }
        }
        return serviceMeta;
    }

    //获取服务元数据列表中连接数为空的元数据，说明没有连接
    private ServiceMeta getNullServiceMeta(List<ServiceMeta> servers){
        for (int i = 0; i < servers.size(); i++) {
            ServiceMeta serviceMeta = servers.get(i);
            if (ConnectionsContext.getValue(serviceMeta) == null){
                return serviceMeta;
            }
        }
        return null;
    }
}
