package com.ct.rpc.provider.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.ct.rpc.common.helper.RpcServiceHelper;
import com.ct.rpc.common.threadpool.ServerThreadPool;
import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.enumeration.RpcStatus;
import com.ct.rpc.protocol.enumeration.RpcType;
import com.ct.rpc.protocol.header.RpcHeader;
import com.ct.rpc.protocol.request.RpcRequest;
import com.ct.rpc.protocol.response.RpcResponse;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author CT
 * @version 1.0.0
 * @description RPC服务提供者的Handler处理类
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    private final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);

    /**
     * 存储服务提供者中被@RpcService注解标注的类的对象
     * key为：serviceName#serviceVersion#group
     * value为：@RpcService注解标注的类的对象
     */
    private final Map<String, Object> handlerMap;

    public RpcProviderHandler(Map<String, Object> handlerMap){
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) throws Exception {
        ServerThreadPool.submit(() -> {
            RpcHeader header = protocol.getHeader();
            header.setMsgType((byte) RpcType.RESPONSE.getType());

            RpcRequest request = protocol.getBody();
            logger.debug("Receive request + " + header.getRequestId());

            RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
            RpcResponse response = new RpcResponse();
            try {
                Object result = handle(request);
                response.setResult(result);
                response.setAsync(request.isAsync());
                response.setOneway(request.isOneway());
                header.setStatus((byte) RpcStatus.SUCCESS.getCode());
            } catch (Throwable t){
                response.setError(t.toString());
                header.setStatus((byte) RpcStatus.FAIL.getCode());
                logger.error("RPC server handle request error");
            }
            responseRpcProtocol.setHeader(header);
            responseRpcProtocol.setBody(response);

            ctx.writeAndFlush(responseRpcProtocol).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    logger.debug("Send response for requeset" + header.getRequestId());
                }
            });
        });
    }

    /**
     * 处理request，使用key从handlerMap中获取服务对象，进行参数校验，调用真实方法等操作
     * @param request 服务请求
     * @return
     * @throws Throwable t
     */
    private Object handle(RpcRequest request) throws Throwable{
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        Object serviceBean = handlerMap.get(serviceKey);
        if (serviceBean == null){
            throw new RuntimeException(String.format("service not exist: %s:%s",request.getClassName(), request.getMethodName()));
        }

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        logger.debug(serviceClass.getName());
        logger.debug(methodName);
        if (parameterTypes !=null && parameterTypes.length > 0){
            for (int i = 0; i < parameterTypes.length; ++i) {
                logger.debug(parameterTypes[i].getName());
            }
        }

        if (parameters !=null && parameters.length > 0){
            for (int i = 0; i < parameters.length; ++i) {
                logger.debug(parameters[i].toString());
            }
        }
        return invokeMethod(serviceBean, serviceClass, methodName, parameterTypes, parameters);
    }

    /**
     * 通过反射调用真实的方法
     * @param serviceBean 服务对象
     * @param serviceClass 服务对象的类
     * @param methodName 方法
     * @param parameterTypes 参数类型数组
     * @param parameters 具体参数数组
     * @return 返回值
     * @throws Throwable t
     */
    //todo 现在使用jdk动态代理的方式
    private Object invokeMethod(Object serviceBean, Class<?> serviceClass, String methodName, Class<?>[] parameterTypes, Object[] parameters) throws Throwable{
        Method method = serviceClass.getMethod(methodName, parameterTypes);
        method.setAccessible(true);
        return method.invoke(serviceBean, parameters);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("server caught exception",cause);
        ctx.close();
    }
}
