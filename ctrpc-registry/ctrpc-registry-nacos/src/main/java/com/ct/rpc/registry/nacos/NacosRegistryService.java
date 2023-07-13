package com.ct.rpc.registry.nacos;

import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import com.ct.rpc.loadbalancer.api.ServiceLoadBalancer;
import com.ct.rpc.protocol.meta.ServiceMeta;
import com.ct.rpc.registry.api.RegistryService;
import com.ct.rpc.registry.api.base.BaseRegistryService;
import com.ct.rpc.registry.api.config.RegistryConfig;
import com.ct.rpc.spi.annotation.SPIClass;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPIClass
public class NacosRegistryService extends BaseRegistryService {

    private static Logger logger = LoggerFactory.getLogger(NacosRegistryService.class);

    private NamingService namingService;

    @Override
    public void init(RegistryConfig registryConfig) throws Exception {
        logger.info("初始化Nacos客户端...");
        this.namingService = NamingFactory.createNamingService(registryConfig.getRegistryAddr());
        initLoadBalancer(registryConfig);
    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        final String serviceKey = buildServiceKey(serviceMeta);
        final String instanceId = buildInstanceId(serviceMeta);

        Instance instance = new Instance();
        instance.setInstanceId(instanceId);
        instance.setServiceName(serviceKey);
        instance.setIp(serviceMeta.getServiceAddr());
        instance.setPort(serviceMeta.getServicePort());
        instance.setHealthy(true);
        instance.setMetadata(ImmutableMap.of(META, buildMetaString(serviceMeta)));

        namingService.registerInstance(serviceKey, instance);
    }

    @Override
    public void unregister(ServiceMeta serviceMeta) throws Exception {
        namingService.deregisterInstance(buildServiceKey(serviceMeta), serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode, String sourceIP) throws Exception {
        List<Instance> instances = namingService.getAllInstances(serviceName);
        if (instances == null || instances.isEmpty()){
            return null;
        }
        final List<ServiceMeta> serviceMetaList = instances.stream().map(instanceInfo -> parseMetaString(instanceInfo.getMetadata().get(META))).collect(Collectors.toList());
        return selectService(invokerHashCode, sourceIP, serviceMetaList);
    }

    @Override
    public ServiceMeta select(List<ServiceMeta> serviceMetaList, int invokeHashCode, String sourceIp) {
        return null;
    }

    @Override
    public void destroy() throws Exception {
        namingService.shutDown();
    }
}
