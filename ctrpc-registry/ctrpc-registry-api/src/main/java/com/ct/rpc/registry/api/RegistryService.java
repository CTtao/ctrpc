package com.ct.rpc.registry.api;

import com.ct.rpc.protocol.meta.ServiceMeta;
import com.ct.rpc.registry.api.config.RegistryConfig;
import com.ct.rpc.spi.annotation.SPI;

import java.io.IOException;
import java.util.List;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPI
public interface RegistryService {

    /** 服务注册
     * @param serviceMeta 服务元数据
     * @throws Exception 抛出异常
     */
    void register(ServiceMeta serviceMeta) throws Exception;
    /**
     * 服务取消注册
     * @param serviceMeta 服务元数据
     * @throws Exception 抛出异常
     */
    void unregister(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务发现
     * @param serviceName 服务名称
     * @param invokerHashCode HashCode值
     * @param sourceIP 源IP
     * @return 服务元数据
     * @throws Exception 抛出异常
     */
    ServiceMeta discovery(String serviceName, int invokerHashCode, String sourceIP) throws Exception;

    /**
     * 从多个元数据列表中根据一定的规则获取一个元数据
     * @param serviceMetaList 元数据列表
     * @return 某个特定的元数据
     */
    ServiceMeta select(List<ServiceMeta> serviceMetaList, int invokeHashCode, String sourceIp);

    /**
     * 获取所有数据
     */
    List<ServiceMeta> discoveryAll() throws Exception;
    /**
     * 服务销毁
     * @throws IOException 抛出异常
     */
    void destroy() throws Exception;

    /**
     * 默认初始化方法
     */
    default void init(RegistryConfig registryConfig) throws Exception{

    }
}
