package com.ct.rpc.demo.spring.boot.consumer.service.impl;

import com.ct.rpc.annotation.RpcReference;
import com.ct.rpc.demo.api.DemoService;
import com.ct.rpc.demo.spring.boot.consumer.service.ConsumerDemoService;
import org.springframework.stereotype.Service;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@Service
public class ConsumerDemoServiceImpl implements ConsumerDemoService {
    @RpcReference(registryType = "zookeeper",
            registryAddress = "127.0.0.1:2181",
            loadBalanceType = "zkconsistenthash",
            version = "1.0.0",
            group = "ct",
            serializationType = "protostuff",
            proxy = "cglib",
            timeout = 30000,
            async = false,
            oneway = false)
    private DemoService demoService;

    @Override
    public String hello(String name) {
        return demoService.hello(name);
    }
}
