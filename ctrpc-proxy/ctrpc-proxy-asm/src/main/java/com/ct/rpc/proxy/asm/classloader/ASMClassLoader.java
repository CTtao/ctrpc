package com.ct.rpc.proxy.asm.classloader;

import java.util.HashMap;
import java.util.Map;

/**
 * @author CT
 * @version 1.0.0
 * @description 自定义类加载器
 */
public class ASMClassLoader extends ClassLoader{
    private final Map<String, byte[]> classMap = new HashMap<>();

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        if (classMap.containsKey(name)){
            byte[] bytes = classMap.get(name);
            classMap.remove(name);
            return defineClass(name, bytes, 0, bytes.length);
        }
        return super.findClass(name);
    }

    public void add(String name, byte[] bytes){
        classMap.put(name, bytes);
    }
}
