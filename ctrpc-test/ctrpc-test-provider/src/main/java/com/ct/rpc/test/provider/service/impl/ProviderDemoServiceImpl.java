package com.ct.rpc.test.provider.service.impl;

import com.ct.rpc.annotation.RpcService;
import com.ct.rpc.test.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@RpcService(interfaceClass = DemoService.class
        ,interfaceClassName = "com.ct.rpc.test.api.DemoService"
        ,version = "1.0.0"
        ,group = "ct")
public class ProviderDemoServiceImpl implements DemoService {
    private final Logger logger = LoggerFactory.getLogger(ProviderDemoServiceImpl.class);
    @Override
    public String hello(String name) {
        logger.info("调用hello方法传入的参数为===>>>{}", name);
        return "hello " + name;
    }
}
