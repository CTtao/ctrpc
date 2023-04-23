package com.ct.rpc.test.scanner.consumer.service.impl;

import com.ct.rpc.annotation.RpcReference;
import com.ct.rpc.test.scanner.consumer.service.ConsumerBusinessService;
import com.ct.rpc.test.scanner.service.DemoService;

/**
 * @author CT
 * @version 1.0.0
 * @description 服务消费者业务逻辑实现类
 */
public class ConsumerBusinessServiceImpl implements ConsumerBusinessService {

    @RpcReference(registryType = "zookeeper",
            registryAddress = "127.0.0.1:2181",
            version = "1.0.0",
            group = "ct")
    private DemoService demoService;
}
