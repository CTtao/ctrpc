package com.ct.rpc.demo.spring.xml.provider;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author CT
 * @version 1.0.0
 * @description 服务提供者启动类
 */
public class SpringXmlProviderStarter {
    public static void main(String[] args) {
        new ClassPathXmlApplicationContext("server-spring.xml");
    }
}
