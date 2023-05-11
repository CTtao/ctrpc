package com.ct.rpc.test.provider.single;

import com.ct.rpc.provider.RpcSingleServer;
import org.junit.Test;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class RpcSingleServerTest {
    @Test
    public void startRpcServiceServer(){
        RpcSingleServer singleServer = new RpcSingleServer("127.0.0.1:27880",
                "127.0.0.1:2181",
                "zookeeper",
                "com.ct.rpc.test",
                "cglib");
        singleServer.startNettyServer();
    }
}
