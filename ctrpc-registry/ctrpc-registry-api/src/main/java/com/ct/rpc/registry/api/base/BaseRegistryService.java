package com.ct.rpc.registry.api.base;

import com.alibaba.fastjson.JSON;
import com.ct.rpc.common.helper.RpcServiceHelper;
import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.loadbalancer.api.ServiceLoadBalancer;
import com.ct.rpc.protocol.meta.ServiceMeta;
import com.ct.rpc.registry.api.RegistryService;
import com.ct.rpc.registry.api.config.RegistryConfig;
import com.ct.rpc.spi.loader.ExtensionLoader;

import java.util.List;

/**
 * @author CT
 * @version 1.0.0
 * @description 为注册中心服务提供公共方法
 */
public abstract class BaseRegistryService implements RegistryService {
    public static final String META = "meta";

    protected ServiceLoadBalancer<ServiceMeta> serviceLoadBalancer;
    protected ServiceLoadBalancer<ServiceMeta> serviceEnhancedLoadBalancer;

    @SuppressWarnings("unchecked")
    protected void initLoadBalancer(RegistryConfig registryConfig){
        if (registryConfig.getRegistryLoadBalanceType().toLowerCase().contains(RpcConstants.SERVICE_ENHANCED_LOAD_BALANCER_PREFIX)){
            this.serviceEnhancedLoadBalancer = ExtensionLoader.getExtension(ServiceLoadBalancer.class, registryConfig.getRegistryLoadBalanceType());
        } else {
            this.serviceLoadBalancer = ExtensionLoader.getExtension(ServiceLoadBalancer.class, registryConfig.getRegistryLoadBalanceType());
        }
    }

    protected ServiceMeta selectService(int invokeHashcode, String sourceIP, List<ServiceMeta> serviceMetaList){
        if (serviceLoadBalancer != null){
            return this.serviceLoadBalancer.select(serviceMetaList, invokeHashcode, sourceIP);
        }
        return this.serviceEnhancedLoadBalancer.select(serviceMetaList, invokeHashcode, sourceIP);
    }

    protected String buildServiceKey(ServiceMeta serviceMeta) {
        return RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup());
    }

    protected String buildInstanceId(ServiceMeta serviceMeta){
        String serviceKey = buildServiceKey(serviceMeta);
        return String.join("#", serviceKey, serviceMeta.getServiceAddr(), String.valueOf(serviceMeta.getServicePort()));
    }

    protected String buildMetaString(ServiceMeta serviceMeta){
        return JSON.toJSONString(serviceMeta);
    }

    protected ServiceMeta parseMetaString(String value){
        return JSON.parseObject(value, ServiceMeta.class);
    }
}
