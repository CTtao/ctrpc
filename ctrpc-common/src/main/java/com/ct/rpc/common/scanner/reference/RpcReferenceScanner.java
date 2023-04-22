package com.ct.rpc.common.scanner.reference;

import com.ct.rpc.annotation.RpcReference;
import com.ct.rpc.annotation.RpcService;
import com.ct.rpc.common.scanner.ClassScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author CT
 * @version 1.0.0
 * @description @RpcReference扫描器
 */
public class RpcReferenceScanner extends ClassScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcReferenceScanner.class);

    public static Map<String, Object> doScannerWithRpcReferenceAnnotationFilter(
            /*String host, int port,*/ String scanPackage /* RegistryService registryService*/
    ) throws Exception{
        Map<String, Object> handlerMap= new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList == null || classNameList.isEmpty()){
            return handlerMap;
        }
        classNameList.stream().forEach(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                Field[] declaredFields = clazz.getDeclaredFields();
                Stream.of(declaredFields).forEach(field -> {
                    RpcReference rpcReference = field.getAnnotation(RpcReference.class);
                    if (rpcReference != null){
                        //todo 处理后续逻辑，将@RpcReference注解标注的接口引用代理对象，放入全局缓存中
                        LOGGER.info("当前标注了@RpcReference注解的字段名称==>>> " + field.getName());
                        LOGGER.info("@RpcService注解上标记的信息如下：");
                        LOGGER.info("version===>>> " + rpcReference.version());
                        LOGGER.info("group===>>> " + rpcReference.group());
                        LOGGER.info("registryType===>>> " + rpcReference.registryType());
                        LOGGER.info("registryAddress===>>> " + rpcReference.registryAddress());
                    }
                });
            } catch (Exception e){
                LOGGER.error("scan classes throws exception: {}",e);
            }
        });
        return handlerMap;
    }
}
