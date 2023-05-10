package com.ct.rpc.reflect.api;

import com.ct.rpc.spi.annotation.SPI;

/**
 * @author CT
 * @version 1.0.0
 * @description 反射方法调用接口
 */
@SPI
public interface ReflectInvoker {

    /**
     * 调用真实方法的SPI通用接口
     * @param serviceBean 方法所在的对象实例
     * @param serviceClass 方法所在对象实例的Class对象
     * @param methodName 方法的名称
     * @param parameterTypes 方法的参数类型
     * @param parameters 方法的参数数组
     * @return 方法调用的结果信息
     * @throws Throwable 抛出的异常
     */
    Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable;
}
