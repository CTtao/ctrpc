package com.ct.rpc.reflect.cglib;

import com.ct.rpc.reflect.api.ReflectInvoker;
import com.ct.rpc.spi.annotation.SPIClass;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CT
 * @version 1.0.0
 * @description 反射调用方法的类
 */
@SPIClass
public class CglibReflectInvoker implements ReflectInvoker {
    private final Logger logger = LoggerFactory.getLogger(CglibReflectInvoker.class);

    @Override
    public Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable {
        logger.info("use cglib reflect type invoke method...");
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastClassMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastClassMethod.invoke(serviceBean, parameters);
    }
}
