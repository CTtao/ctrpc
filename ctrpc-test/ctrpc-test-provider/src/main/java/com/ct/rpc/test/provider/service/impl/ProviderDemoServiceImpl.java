package com.ct.rpc.test.provider.service.impl;

import com.ct.rpc.annotation.RpcService;
import com.ct.rpc.test.provider.service.DemoService;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@RpcService(interfaceClass = DemoService.class
        ,interfaceClassName = "com.ct.rpc.test.provider.service.DemoService"
        ,version = "1.0.0"
        ,group = "ct")
public class ProviderDemoServiceImpl implements DemoService {
}
