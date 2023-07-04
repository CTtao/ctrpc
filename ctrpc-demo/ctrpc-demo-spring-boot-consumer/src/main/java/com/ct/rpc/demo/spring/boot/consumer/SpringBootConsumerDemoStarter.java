package com.ct.rpc.demo.spring.boot.consumer;

import com.ct.rpc.demo.spring.boot.consumer.service.ConsumerDemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.ct.rpc"})
public class SpringBootConsumerDemoStarter {
    private static final Logger LOGGER = LoggerFactory.getLogger(SpringBootConsumerDemoStarter.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SpringBootConsumerDemoStarter.class, args);
        ConsumerDemoService consumerDemoService = context.getBean(ConsumerDemoService.class);
        for (int i = 0; i < 5; i++) {
            String result = consumerDemoService.hello("ct");
            LOGGER.info("返回的结果数据===>>> " + result);
        }
    }
}
