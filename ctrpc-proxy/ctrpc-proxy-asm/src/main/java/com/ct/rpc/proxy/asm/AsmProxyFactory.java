package com.ct.rpc.proxy.asm;

import com.ct.rpc.proxy.api.BaseProxyFactory;
import com.ct.rpc.proxy.api.ProxyFactory;
import com.ct.rpc.proxy.asm.proxy.ASMProxy;
import com.ct.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CT
 * @version 1.0.0
 * @description ASM动态代理
 */
@SPIClass
public class AsmProxyFactory<T> extends BaseProxyFactory<T> implements ProxyFactory {
    private final Logger logger = LoggerFactory.getLogger(AsmProxyFactory.class);

    @Override
    public <T> T getProxy(Class<T> clazz) {
        try {
            logger.info("基于ASM动态代理...");
            return (T) ASMProxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, objectProxy);
        } catch (Exception e){
            logger.error("asm proxy throws exception:{}", e);
        }
        return null;
    }
}
