package com.ct.rpc.proxy.api.consumer;

import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.request.RpcRequest;
import com.ct.rpc.proxy.api.future.RpcFuture;
import com.ct.rpc.registry.api.RegistryService;

/**
 * @author CT
 * @version 1.0.0
 * @description 服务消费类
 */
public interface Consumer {

    /**
     * 消费者发送请求
     */
    RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception;
}
