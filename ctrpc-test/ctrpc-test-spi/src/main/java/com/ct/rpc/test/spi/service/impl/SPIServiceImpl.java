package com.ct.rpc.test.spi.service.impl;

import com.ct.rpc.spi.annotation.SPIClass;
import com.ct.rpc.test.spi.service.SPIService;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPIClass
public class SPIServiceImpl implements SPIService {
    @Override
    public String hello(String name) {
        return "hello " + name;
    }
}
