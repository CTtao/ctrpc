package com.ct.rpc.provider.common.server.api;

/**
 * @author CT
 * @version 1.0.0
 * @description 启动RPC服务的接口
 */
public interface Server {

    /**
     * 启动Netty服务
     */
    void startNettyServer();
}
