package com.ct.rpc.serialization.api;

import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.spi.annotation.SPI;

/**
 * @author CT
 * @version 1.0.0
 * @description 序列化接口
 */
@SPI(RpcConstants.SERIALIZATION_JDK)
public interface Serialization {

    /**
     * 序列化
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化
     * @param data
     * @param cls
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> cls);
}
