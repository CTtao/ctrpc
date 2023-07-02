package com.ct.rpc.demo.spring.annotation.consumer.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author CT
 * @version 1.0.0
 * @description 服务消费者注解配置类
 */
@Configuration
@ComponentScan(value = {"com.ct.rpc.*"})
public class SpringAnnotationConsumerConfig {
}
