package com.ct.rpc.provider;

import com.ct.rpc.provider.common.scanner.RpcServiceScanner;
import com.ct.rpc.provider.common.server.base.BaseServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CT
 * @version 1.0.0
 * @description 以Java原生方式启动启动Rpc
 */
public class RpcSingleServer extends BaseServer {
    private final Logger logger = LoggerFactory.getLogger(RpcSingleServer.class);

    public RpcSingleServer(String serverAddress, String registerAddress,
                           String registerType, String registryLoadBalanceType,
                           String scanPackage, String reflectType,
                           int heartbeatInterval, int scanNotActiveChannelInterval,
                           boolean enableResultCache, int resultCacheExpire,
                           int corePoolSize, int maxPoolSize) {
        super(serverAddress, registerAddress, registerType, registryLoadBalanceType, reflectType,
                heartbeatInterval, scanNotActiveChannelInterval,
                enableResultCache, resultCacheExpire,
                corePoolSize, maxPoolSize);
        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(this.host, this.port, scanPackage, registryService);
        } catch (Exception e){
            logger.error("RPC server init error", e);
        }
    }
}
