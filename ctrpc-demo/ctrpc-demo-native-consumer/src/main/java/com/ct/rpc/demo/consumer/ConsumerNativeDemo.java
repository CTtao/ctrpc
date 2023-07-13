package com.ct.rpc.demo.consumer;

import com.ct.rpc.consumer.RpcClient;
import com.ct.rpc.demo.api.DemoService;
import com.ct.rpc.proxy.api.async.IAsyncObjectProxy;
import com.ct.rpc.proxy.api.future.RpcFuture;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CT
 * @version 1.0.0
 * @description 服务消费者
 */
public class ConsumerNativeDemo {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerNativeDemo.class);

    private RpcClient rpcClient;

    @Before
    public void initRpcClient(){
        rpcClient = new RpcClient("127.0.0.1:2181",
                "zookeeper",
                "enhanced_leastconnections",
                "asm",
                "1.0.0",
                "ct",
                "protostuff",
                3000,
                false,
                false,
                30000, 60000,
                1000, 3,
                false, 10000,
                true, "127.0.0.1:27880,127.0.0.1:27880,127.0.0.1:27880");
    }

    @Test
    public void testInterfaceRpc() throws InterruptedException{
        DemoService demoService = rpcClient.create(DemoService.class);
        Thread.sleep(5000);
        String result = demoService.hello("ct");
        LOGGER.info("返回的结果数据为===>>>" + result);
//        rpcClient.shutdown();
        while (true){
            Thread.sleep(1000);
        }
    }

    @Test
    public void testAsyncInterfaceRpc() throws Exception{
        IAsyncObjectProxy demoService = rpcClient.createAsync(DemoService.class);
        RpcFuture future = demoService.call("hello", "ct");
        LOGGER.info("返回的结果数据为===>>>" + future.get());
        rpcClient.shutdown();
    }
}
