package com.ct.rpc.demo.spring.annotation.provider;

import com.ct.rpc.demo.spring.annotation.provider.config.SpringAnnotationProviderConfig;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author CT
 * @version 1.0.0
 * @description 基于Spring注解的服务提供者启动类
 */
public class SpringAnnotationProviderStarter {
    public static void main(String[] args) {
        new AnnotationConfigApplicationContext(SpringAnnotationProviderConfig.class);
    }
}
