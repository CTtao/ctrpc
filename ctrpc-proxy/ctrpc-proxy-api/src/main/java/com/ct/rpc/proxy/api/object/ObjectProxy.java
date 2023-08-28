package com.ct.rpc.proxy.api.object;

import com.ct.rpc.cache.result.CacheResultKey;
import com.ct.rpc.cache.result.CacheResultManager;
import com.ct.rpc.common.utils.StringUtils;
import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.enumeration.RpcType;
import com.ct.rpc.protocol.header.RpcHeaderFactory;
import com.ct.rpc.protocol.request.RpcRequest;
import com.ct.rpc.proxy.api.async.IAsyncObjectProxy;
import com.ct.rpc.proxy.api.consumer.Consumer;
import com.ct.rpc.proxy.api.future.RpcFuture;
import com.ct.rpc.reflect.api.ReflectInvoker;
import com.ct.rpc.registry.api.RegistryService;
import com.ct.rpc.spi.loader.ExtensionLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class ObjectProxy<T> implements IAsyncObjectProxy, InvocationHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(ObjectProxy.class);

    private Class<T> clazz;

    private String serviceVersion;

    private String serviceGroup;

    private long timeout = 15000;

    private RegistryService registryService;

    private Consumer consumer;

    private String serializationType;

    private boolean async;

    private boolean oneway;

    /**
     * 是否开启结果缓存
     */
    private boolean enableResultCache;

    /**
     * 结果缓存管理器
     */
    private CacheResultManager<Object> cacheResultManager;

    /**
     * 反射调用方法
     */
    private ReflectInvoker reflectInvoker;

    /**
     * 容错Class类
     */
    private Class<?> fallbackClass;

    public ObjectProxy(Class<T> clazz){
        this.clazz = clazz;
    }

    public ObjectProxy(Class<T> clazz, String serviceVersion, String serviceGroup, String serializationType, long timeout,
                       RegistryService registryService, Consumer consumer,
                       boolean async, boolean oneway,
                       boolean enableResultCache, int resultCacheExpire,
                       String reflectType, String fallbackClassName, Class<?> fallbackClass) {
        this.clazz = clazz;
        this.serviceVersion = serviceVersion;
        this.serviceGroup = serviceGroup;
        this.timeout = timeout;
        this.consumer = consumer;
        this.serializationType = serializationType;
        this.async = async;
        this.oneway = oneway;
        this.registryService = registryService;
        this.enableResultCache = enableResultCache;
        if (resultCacheExpire <= 0){
            resultCacheExpire = RpcConstants.RPC_SCAN_RESULT_CACHE_EXPIRE;
        }
        this.cacheResultManager = CacheResultManager.getInstance(resultCacheExpire, enableResultCache);
        this.reflectInvoker = ExtensionLoader.getExtension(ReflectInvoker.class, reflectType);
        this.fallbackClass = getFallbackClass(fallbackClassName, fallbackClass);
    }

    /**
     * 优先使用fallbackClass，如果fallbackClass为空，则使用fallbackClassName
     */
    private Class<?> getFallbackClass(String fallbackClassName, Class<?> fallbackClass){
        if (this.isFallbackClassEmpty(fallbackClass)){
            try {
                if (!StringUtils.isEmpty(fallbackClassName)){
                    fallbackClass = Class.forName(fallbackClassName);
                }
            } catch (ClassNotFoundException e){
                LOGGER.error(e.getMessage());
            }
        }
        return fallbackClass;
    }

    /**
     * 容错class为空
     */
    private boolean isFallbackClassEmpty(Class<?> fallbackClass){
        return fallbackClass == null
                || fallbackClass == RpcConstants.DEFAULT_FALLBACK_CLASS
                || RpcConstants.DEFAULT_FALLBACK_CLASS.equals(fallbackClass);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (Object.class == method.getDeclaringClass()){
            String name = method.getName();
            if ("equals".equals(name)){
                return proxy == args[0];
            } else if ("hashCode".equals(name)){
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)){
                return proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy)) + ", with InvocationHandler" + this;
            } else {
                throw new IllegalStateException(String.valueOf(method));
            }
        }
        //开启缓存，直接调用方法请求服务提供者
        if (enableResultCache) return invokeSendRequestMethodCache(method, args);
        return invokeSendRequestMethod(method, args);
    }

    private Object invokeSendRequestMethodCache(Method method, Object[] args) throws Exception{
        //开启缓存，则处理缓存
        CacheResultKey cacheResultKey = new CacheResultKey(method.getDeclaringClass().getName(), method.getName(), method.getParameterTypes(), args, serviceVersion, serviceGroup);
        Object obj = this.cacheResultManager.get(cacheResultKey);
        if (obj == null){
            obj = invokeSendRequestMethod(method, args);
            if (obj != null){
                cacheResultKey.setCacheTimeStamp(System.currentTimeMillis());
                this.cacheResultManager.put(cacheResultKey, obj);
            }
        }
        return obj;
    }

    /**
     * 真正调用远程方法
     */
    private Object invokeSendRequestMethod(Method method, Object[] args) throws Exception{
        try {
            RpcProtocol<RpcRequest> requestRpcProtocol = getSendRequest(method, args);
            RpcFuture rpcFuture = this.consumer.sendRequest(requestRpcProtocol, registryService);
            return rpcFuture == null ? null : timeout > 0 ? rpcFuture.get(timeout, TimeUnit.MILLISECONDS) : rpcFuture.get();
        } catch (Throwable t){
            //fallback不为空，则执行容错处理
            if (this.isFallbackClassEmpty(fallbackClass)){
                return null;
            }
            return getFallbackResult(method, args);
        }
    }

    private Object getFallbackResult(Method method, Object[] args){
        try {
            return reflectInvoker.invokeMethod(fallbackClass.newInstance(), fallbackClass, method.getName(), method.getParameterTypes(), args);
        } catch (Throwable t){
            LOGGER.error(t.getMessage());
        }
        return null;
    }

    private RpcProtocol<RpcRequest> getSendRequest(Method method, Object[] args){
        RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
        requestRpcProtocol.setHeader(RpcHeaderFactory.getRequestHeader(serializationType, RpcType.REQUEST.getType()));

        RpcRequest request = new RpcRequest();
        request.setVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setGroup(this.serviceGroup);
        request.setParameters(args);
        request.setAsync(async);
        request.setOneway(oneway);
        requestRpcProtocol.setBody(request);
        //Debug
        LOGGER.debug(method.getDeclaringClass().getName());
        LOGGER.debug(method.getName());

        if (method.getParameterTypes() != null && method.getParameterTypes().length > 0){
            for (int i = 0; i < method.getParameterTypes().length; i++) {
                LOGGER.debug(method.getParameterTypes()[i].getName());
            }
        }
        if (args != null && args.length > 0){
            for (int i = 0; i < args.length; i++) {
                LOGGER.debug(args[i].toString());
            }
        }
        return requestRpcProtocol;
    }

    @Override
    public RpcFuture call(String funcName, Object... args) {
        RpcProtocol<RpcRequest> request = getCallRequest(this.clazz.getName(), funcName, args);
        RpcFuture rpcFuture = null;
        try {
            rpcFuture = this.consumer.sendRequest(request,registryService);
        } catch (Exception e){
            LOGGER.error("async all throws exception:{}", e);
        }
        return rpcFuture;
    }

    private RpcProtocol<RpcRequest> getCallRequest(String className, String methodName, Object[] args){
        RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
        requestRpcProtocol.setHeader(RpcHeaderFactory.getRequestHeader(serializationType, RpcType.REQUEST.getType()));

        RpcRequest request = new RpcRequest();
        request.setClassName(className);
        request.setMethodName(methodName);
        request.setParameters(args);
        request.setVersion(this.serviceVersion);
        request.setGroup(this.serviceGroup);

        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = getClassType(args[i]);
        }
        request.setParameterTypes(parameterTypes);
        requestRpcProtocol.setBody(request);

        //Debug
        LOGGER.debug(className);
        LOGGER.debug(methodName);

        for (int i = 0; i < parameterTypes.length; i++) {
            LOGGER.debug(parameterTypes[i].getName());
        }
        for (int i = 0; i < args.length; i++) {
            LOGGER.debug(args[i].toString());
        }
        return requestRpcProtocol;
    }

    private Class<?> getClassType(Object obj){
        Class<?> classType = obj.getClass();
        String typeName = classType.getName();
        switch (typeName){
            case "java.lang.Integer":
                return Integer.TYPE;
            case "java.lang.Long":
                return Long.TYPE;
            case "java.lang.Float":
                return Float.TYPE;
            case "java.lang.Double":
                return Double.TYPE;
            case "java.lang.Character":
                return Character.TYPE;
            case "java.lang.Boolean":
                return Boolean.TYPE;
            case "java.lang.Short":
                return Short.TYPE;
            case "java.lang.Byte":
                return Byte.TYPE;
        }
        return classType;
    }
}
