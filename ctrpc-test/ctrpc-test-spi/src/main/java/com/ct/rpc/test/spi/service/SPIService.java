package com.ct.rpc.test.spi.service;

import com.ct.rpc.spi.annotation.SPI;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPI("spiService")
public interface SPIService {
    String hello(String name);
}
