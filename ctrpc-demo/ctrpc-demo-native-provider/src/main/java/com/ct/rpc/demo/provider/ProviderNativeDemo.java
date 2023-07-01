package com.ct.rpc.demo.provider;

import com.ct.rpc.provider.RpcSingleServer;
import org.junit.Test;

/**
 * @author CT
 * @version 1.0.0
 * @description 服务提供者
 */
public class ProviderNativeDemo {

    @Test
    public void startRpcSingleServer(){
        RpcSingleServer singleServer = new RpcSingleServer("127.0.0.1:27880",
                "127.0.0.1:2181",
                "zookeeper",
                "random",
                "com.ct.rpc.demo",
                "asm",
                3000,
                6000);
        singleServer.startNettyServer();
    }
}
