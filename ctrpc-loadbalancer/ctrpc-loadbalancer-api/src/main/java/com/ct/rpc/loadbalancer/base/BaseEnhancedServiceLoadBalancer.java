package com.ct.rpc.loadbalancer.base;

import com.ct.rpc.loadbalancer.api.ServiceLoadBalancer;
import com.ct.rpc.protocol.meta.ServiceMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

/**
 * @author CT
 * @version 1.0.0
 * @description 基础的增强型负载均衡类
 */
public abstract class BaseEnhancedServiceLoadBalancer implements ServiceLoadBalancer<ServiceMeta> {

    /**
     *  根据权重重新生成服务元数据列表，权重越高的元数据，会在最终的列表中出现的次数越多
     *  例如，权重为1，最终出现1次，权重为2，最终出现2次，权重为3，最终出现3次，依此类推...
     */
    protected List<ServiceMeta> getWeightServiceMetaList(List<ServiceMeta> servers){
        if (servers == null || servers.isEmpty()){
            return null;
        }
        List<ServiceMeta> serviceMetaList = new ArrayList<>();
        servers.stream().forEach(server ->{
            IntStream.range(0, server.getWeight()).forEach(i -> {
                serviceMetaList.add(server);
            });
        });
        return serviceMetaList;
    }
}
