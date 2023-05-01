package com.ct.rpc.test.consumer;

import com.ct.rpc.consumer.RpcClient;
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

    public static void main(String[] args) {
        RpcClient rpcClient = new RpcClient("1.0.0", "ct", "jdk", 3000, false, false);
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("ct");
        LOGGER.info("返回的结果数据为===>>>" + result);
        rpcClient.shutdown();
    }
}
