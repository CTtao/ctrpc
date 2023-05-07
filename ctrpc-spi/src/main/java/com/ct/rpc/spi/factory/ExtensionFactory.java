package com.ct.rpc.spi.factory;

import com.ct.rpc.spi.annotation.SPI;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPI("spi")
public interface ExtensionFactory {

    /**
     * 获取扩展类对象
     * @param key 传入的key
     * @param clazz class类型
     * @param <T> 泛型
     * @return 扩展类对象
     */
    <T> T getExtension(String key, Class<T> clazz);
}
