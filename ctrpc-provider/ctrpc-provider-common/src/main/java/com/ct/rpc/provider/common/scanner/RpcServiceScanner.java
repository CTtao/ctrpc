package com.ct.rpc.provider.common.scanner;

import com.ct.rpc.annotation.RpcService;
import com.ct.rpc.common.helper.RpcServiceHelper;
import com.ct.rpc.common.scanner.ClassScanner;
import com.ct.rpc.protocol.meta.ServiceMeta;
import com.ct.rpc.registry.api.RegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author CT
 * @version 1.0.0
 * @description @RpcService扫描器
 */
public class RpcServiceScanner extends ClassScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcServiceScanner.class);

    /**
     * 扫描指定包下的类，并筛选使用了@RpcService注解的类
     */
    public static Map<String, Object> doScannerWithRpcServiceAnnotationFilterAndRegistryService (String host, int port, String scanPackage, RegistryService registryService) throws Exception{
       Map<String, Object> handlerMap= new HashMap<>();
        List<String> classNameList = getClassNameList(scanPackage);
        if (classNameList == null || classNameList.isEmpty()){
            return handlerMap;
        }
        classNameList.stream().forEach(className -> {
            try {
                Class<?> clazz = Class.forName(className);
                RpcService rpcService = clazz.getAnnotation(RpcService.class);
                if (rpcService != null){
                    //优先使用interfaceClass，interfaceClass的name为空，再使用interfaceClassName
                    // 向注册中心注册服务，同时向handlerMap中记录标记了RpcService注解的类实例
                    ServiceMeta serviceMeta = new ServiceMeta(getServiceName(rpcService), rpcService.version(), rpcService.group(), host, port);
                    registryService.register(serviceMeta);
                    handlerMap.put(
                            RpcServiceHelper.buildServiceKey(
                                    serviceMeta.getServiceName(),
                                    serviceMeta.getServiceVersion(),
                                    serviceMeta.getServiceGroup())
                            , clazz.newInstance());
                }
            } catch (Exception e){
                LOGGER.error("scan classes throws exception: {}",e);
            }
        });
        return handlerMap;
    }

    private static String getServiceName(RpcService rpcService){
        Class<?> clazz = rpcService.interfaceClass();
        if (clazz == void.class){
            return rpcService.interfaceClassName();
        }
        String serviceName = clazz.getName();
        if (serviceName == null || serviceName.trim().isEmpty()){
            serviceName = rpcService.interfaceClassName();
        }
        return serviceName;
    }
}
