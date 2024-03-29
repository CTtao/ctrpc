package com.ct.rpc.demo.provider.impl;

import com.ct.rpc.annotation.RpcService;
import com.ct.rpc.common.exception.RpcException;
import com.ct.rpc.demo.api.DemoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@RpcService(interfaceClass = DemoService.class, interfaceClassName = "com.ct.rpc.demo.api.DemoService", version = "1.0.0", group = "ct", weight = 2)
public class ProviderDemoServiceImpl implements DemoService {
    private final Logger logger = LoggerFactory.getLogger(ProviderDemoServiceImpl.class);

    @Override
    public String hello(String name) {
        logger.info("调用hello方法传入的参数为===>>>{}", name);
        if ("ct".equals(name)){
            throw new RpcException("rpc provider throws exception");
        }
        return "hello " + name;
    }
}
