package com.ct.rpc.demo.spring.xml.consumer;

import com.ct.rpc.consumer.RpcClient;
import com.ct.rpc.demo.api.DemoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:client-spring.xml")
public class SpringXmlConsumerTest {
    private static Logger logger = LoggerFactory.getLogger(SpringXmlConsumerTest.class);

    @Autowired
    private RpcClient rpcClient;

    @Test
    public void testInterfaceRpc() throws InterruptedException{
        DemoService demoService = rpcClient.create(DemoService.class);
        String result = demoService.hello("ct");
        logger.info("返回的结果数据===>>>" + result);
        //rpcClient.shutdown();
        while (true){
            Thread.sleep(1000);
        }
    }
}
