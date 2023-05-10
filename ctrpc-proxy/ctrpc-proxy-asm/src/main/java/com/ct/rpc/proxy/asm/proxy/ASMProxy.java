package com.ct.rpc.proxy.asm.proxy;

import com.ct.rpc.proxy.asm.classloader.ASMClassLoader;
import com.ct.rpc.proxy.asm.factory.ASMGenerateProxyFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author CT
 * @version 1.0.0
 * @description 自定义ASM代理
 */
public class ASMProxy {
    protected InvocationHandler h;

    private static final AtomicInteger PROXY_INT = new AtomicInteger(0);
    private static final String PROXY_CLASS_NAME_PRE = "$Proxy";

    public ASMProxy(InvocationHandler var1) {
        this.h = var1;
    }

    public static Object newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h) throws Exception{
        //生成代理类Class
        Class<?> proxyClass = generate(interfaces);
        Constructor<?> constructor = proxyClass.getConstructor(InvocationHandler.class);
        return constructor.newInstance(h);
    }

    /**
     * 生成代理的class
     * @param interfaces 接口的Class类型
     * @return 代理的class对象
     * @throws ClassNotFoundException
     */
    private static Class<?> generate(Class<?>[] interfaces) throws ClassNotFoundException{
        String proxyClassName = PROXY_CLASS_NAME_PRE + PROXY_INT.getAndIncrement();
        byte[] codes = ASMGenerateProxyFactory.generateClass(interfaces, proxyClassName);
        //使用自定义的类加载器加载字节码
        ASMClassLoader asmClassLoader = new ASMClassLoader();
        asmClassLoader.add(proxyClassName, codes);
        return asmClassLoader.loadClass(proxyClassName);
    }
}
