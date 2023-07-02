package com.ct.rpc.demo.spring.boot.provider;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SpringBootApplication
@ComponentScan("com.ct.rpc")
public class SpringBootProviderDemoStarter {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootProviderDemoStarter.class, args);
    }
}
