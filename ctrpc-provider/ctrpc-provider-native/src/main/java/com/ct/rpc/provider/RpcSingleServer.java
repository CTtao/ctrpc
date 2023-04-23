package com.ct.rpc.provider;

import com.ct.rpc.common.scanner.server.RpcServiceScanner;
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

    public RpcSingleServer(String serverAddress, String scanPackage) {
        super(serverAddress);
        try {
            this.handlerMap = RpcServiceScanner.doScannerWithRpcServiceAnnotationFilterAndRegistryService(scanPackage);
        } catch (Exception e){
            logger.error("RPC server init error", e);
        }
    }
}
