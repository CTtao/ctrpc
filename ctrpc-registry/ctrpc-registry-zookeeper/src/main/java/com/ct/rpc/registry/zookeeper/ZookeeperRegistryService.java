package com.ct.rpc.registry.zookeeper;

import com.ct.rpc.common.helper.RpcServiceHelper;
import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.loadbalancer.api.ServiceLoadBalancer;
import com.ct.rpc.loadbalancer.helper.ServiceLoadBalancerHelper;
import com.ct.rpc.loadbalancer.random.RandomServiceLoadBalancer;
import com.ct.rpc.protocol.meta.ServiceMeta;
import com.ct.rpc.registry.api.RegistryService;
import com.ct.rpc.registry.api.config.RegistryConfig;
import com.ct.rpc.spi.loader.ExtensionLoader;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class ZookeeperRegistryService implements RegistryService {

    public static final int BASE_SLEEP_TIME_MS = 1000;
    public static final int MAX_RETRIES = 3;
    public static final String ZK_BASE_PATH = "/ct_rpc";

    private ServiceDiscovery<ServiceMeta> serviceDiscovery;

    /**
     * 负载均衡
     */
    private ServiceLoadBalancer<ServiceInstance<ServiceMeta>> serviceLoadBalancer;

    private ServiceLoadBalancer<ServiceMeta> serviceEnhancedLoadBalancer;

    @Override
    public void init(RegistryConfig registryConfig) throws Exception {
        CuratorFramework client = CuratorFrameworkFactory.newClient(registryConfig.getRegistryAddr(), new ExponentialBackoffRetry(BASE_SLEEP_TIME_MS, MAX_RETRIES));
        client.start();
        JsonInstanceSerializer<ServiceMeta> serializer = new JsonInstanceSerializer<>(ServiceMeta.class);
        this.serviceDiscovery = ServiceDiscoveryBuilder.builder(ServiceMeta.class)
                .client(client)
                .serializer(serializer)
                .basePath(ZK_BASE_PATH)
                .build();
        this.serviceDiscovery.start();
        //负载均衡策略根据SPI加载
        //this.serviceLoadBalancer = ExtensionLoader.getExtension(ServiceLoadBalancer.class, registryConfig.getRegistryLoadBalanceType());
        //增强型负载均衡策略
        if (registryConfig.getRegistryLoadBalanceType().toLowerCase().contains(RpcConstants.SERVICE_ENHANCED_LOAD_BALANCER_PREFIX)){
            this.serviceEnhancedLoadBalancer = ExtensionLoader.getExtension(ServiceLoadBalancer.class, registryConfig.getRegistryLoadBalanceType());
        } else {
            this.serviceLoadBalancer = ExtensionLoader.getExtension(ServiceLoadBalancer.class, registryConfig.getRegistryLoadBalanceType());
        }
    }

    @Override
    public void register(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()))
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.registerService(serviceInstance);
    }

    @Override
    public void unregister(ServiceMeta serviceMeta) throws Exception {
        ServiceInstance<ServiceMeta> serviceInstance = ServiceInstance
                .<ServiceMeta>builder()
                .name(serviceMeta.getServiceName())
                .address(serviceMeta.getServiceAddr())
                .port(serviceMeta.getServicePort())
                .payload(serviceMeta)
                .build();
        serviceDiscovery.unregisterService(serviceInstance);
    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashCode, String sourceIP) throws Exception {
        Collection<ServiceInstance<ServiceMeta>> serviceInstances = serviceDiscovery.queryForInstances(serviceName);
        if (serviceLoadBalancer != null){
            return getServiceMetaInstance(invokerHashCode, sourceIP, (List<ServiceInstance<ServiceMeta>>) serviceInstances);
        }
        return this.serviceEnhancedLoadBalancer.select(ServiceLoadBalancerHelper.getServiceMetaList((List<ServiceInstance<ServiceMeta>>) serviceInstances), invokerHashCode, sourceIP);
    }

    private ServiceMeta getServiceMetaInstance(int invokeHashCode, String sourceIP, List<ServiceInstance<ServiceMeta>> serviceInstances){
        ServiceInstance<ServiceMeta> instance = this.serviceLoadBalancer.select(serviceInstances, invokeHashCode, sourceIP);
        if (instance != null){
            return instance.getPayload();
        }
        return null;
    }


    @Override
    public void destroy() throws IOException {
        serviceDiscovery.close();
    }
}
