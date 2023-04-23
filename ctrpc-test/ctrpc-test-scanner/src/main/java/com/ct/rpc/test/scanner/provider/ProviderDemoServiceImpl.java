package com.ct.rpc.test.scanner.provider;

import com.ct.rpc.annotation.RpcService;
import com.ct.rpc.test.scanner.service.DemoService;

/**
 * @author CT
 * @version 1.0.0
 * @description DemoService实现类
 */
@RpcService(interfaceClass = DemoService.class,
        interfaceClassName = "com.ct.rpc.test.scanner.service.DemoService",
        version = "1.0.0",
        group = "ct")
public class ProviderDemoServiceImpl implements DemoService {
}
