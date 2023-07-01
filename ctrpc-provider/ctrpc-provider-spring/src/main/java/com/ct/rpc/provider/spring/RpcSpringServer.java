package com.ct.rpc.provider.spring;

import com.ct.rpc.annotation.RpcService;
import com.ct.rpc.common.helper.RpcServiceHelper;
import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.protocol.meta.ServiceMeta;
import com.ct.rpc.provider.common.server.base.BaseServer;
import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Map;

/**
 * @author CT
 * @version 1.0.0
 * @description 基于Spring启动Rpc服务
 */
public class RpcSpringServer extends BaseServer implements ApplicationContextAware, InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(RpcSpringServer.class);

    public RpcSpringServer(String serverAddress, String registryAddress, String registryType, String registryLoadBalanceType, String reflectType,
                           int heartbeatInterval, int scanNotActiveChannelInterval){
        super(serverAddress, registryAddress, registryType, registryLoadBalanceType, reflectType, heartbeatInterval, scanNotActiveChannelInterval);
    }

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        Map<String, Object> serviceBeanMap = context.getBeansWithAnnotation(RpcService.class);
        if (MapUtils.isNotEmpty(serviceBeanMap)){
            for (Object serviceBean : serviceBeanMap.values()) {
                RpcService rpcService = serviceBean.getClass().getAnnotation(RpcService.class);
                ServiceMeta serviceMeta = new ServiceMeta(this.getServiceName(rpcService), rpcService.version(), rpcService.group(), host, port, this.getWeight(rpcService.weight()));
                handlerMap.put(RpcServiceHelper.buildServiceKey(serviceMeta.getServiceName(), serviceMeta.getServiceVersion(), serviceMeta.getServiceGroup()), serviceBean);
                try {
                    registryService.register(serviceMeta);
                } catch (Exception e){
                    logger.error("rpc server init spring exception:{}", e);
                }
            }
        }
    }

    /**
     * 获取ServiceName
     */
    private String getServiceName(RpcService rpcService){
        //优先使用interfaceClass
        Class<?> clazz = rpcService.interfaceClass();
        if (clazz == void.class){
            return rpcService.interfaceClassName();
        }
        String serviceName = clazz.getName();
        if (serviceName == null || serviceName.trim().isEmpty()){
            serviceName = rpcService.interfaceClassName();
        }
        return serviceName;
    }

    /**
     * 获取weight
     */
    private int getWeight(int weight){
        if (weight < RpcConstants.SERVICE_WEIGHT_MIN){
            weight = RpcConstants.SERVICE_WEIGHT_MIN;
        }
        if (weight > RpcConstants.SERVICE_WEIGHT_MAX){
            weight = RpcConstants.SERVICE_WEIGHT_MAX;
        }
        return weight;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.startNettyServer();
    }
}
