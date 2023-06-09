package com.ct.rpc.demo.spring.annotation.consumer;

import com.ct.rpc.demo.spring.annotation.consumer.config.SpringAnnotationConsumerConfig;
import com.ct.rpc.demo.spring.annotation.consumer.service.ConsumerDemoService;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author CT
 * @version 1.0.0
 * @description 基于Spring注解的消费者测试类
 */
public class SpringAnnotationConsumerTest {
    private static Logger logger = LoggerFactory.getLogger(SpringAnnotationConsumerTest.class);

    @Test
    public void testInterfaceRpc(){
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SpringAnnotationConsumerConfig.class);
        ConsumerDemoService consumerDemoService = context.getBean(ConsumerDemoService.class);
        String result = consumerDemoService.hello("ct");
        logger.info("返回的结果数据===>>> " + result);
    }
}
