package com.ct.rpc.test.spi;

import com.ct.rpc.spi.loader.ExtensionLoader;
import com.ct.rpc.test.spi.service.SPIService;
import org.junit.Test;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class SpiTest {
    @Test
    public void testSpiLoader(){
        SPIService spiService = ExtensionLoader.getExtension(SPIService.class, "spiService");
        String result = spiService.hello("ct");
        System.out.println(result);
    }
}
