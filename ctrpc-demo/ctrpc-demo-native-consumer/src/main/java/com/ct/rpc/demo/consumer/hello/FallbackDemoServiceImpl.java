package com.ct.rpc.demo.consumer.hello;

import com.ct.rpc.demo.api.DemoService;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class FallbackDemoServiceImpl implements DemoService {
    @Override
    public String hello(String name) {
        return "fallback hello " + name;
    }
}
