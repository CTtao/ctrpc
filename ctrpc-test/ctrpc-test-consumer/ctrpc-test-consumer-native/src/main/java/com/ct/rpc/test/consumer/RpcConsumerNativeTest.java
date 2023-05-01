package com.ct.rpc.test.consumer;

import com.ct.rpc.consumer.RpcClient;
import com.ct.rpc.proxy.api.async.IAsyncObjectProxy;
import com.ct.rpc.proxy.api.future.RpcFuture;
import com.ct.rpc.test.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class RpcConsumerNativeTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConsumerNativeTest.class);

    public static void main(String[] args) throws Exception{
        RpcClient rpcClient = new RpcClient("1.0.0", "ct", "jdk", 3000, false, false);
        IAsyncObjectProxy demoService = rpcClient.createAsync(DemoService.class);
        RpcFuture future = demoService.call("hello","ct");
        LOGGER.info("返回的结果数据为===>>>" + future.get());
        rpcClient.shutdown();
    }
}
