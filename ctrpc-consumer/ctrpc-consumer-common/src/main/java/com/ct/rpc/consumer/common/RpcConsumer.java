package com.ct.rpc.consumer.common;

import com.ct.rpc.common.helper.RpcServiceHelper;
import com.ct.rpc.common.ip.IPUtils;
import com.ct.rpc.common.threadpool.ClientThreadPool;
import com.ct.rpc.consumer.common.helper.RpcConsumerHandlerHelper;
import com.ct.rpc.protocol.meta.ServiceMeta;
import com.ct.rpc.proxy.api.consumer.Consumer;
import com.ct.rpc.proxy.api.future.RpcFuture;
import com.ct.rpc.consumer.common.handler.RpcConsumerHandler;
import com.ct.rpc.consumer.common.initializer.RpcConsumerInitializer;
import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.request.RpcRequest;
import com.ct.rpc.registry.api.RegistryService;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class RpcConsumer implements Consumer {
    private final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    private final String localIP;

    private static volatile RpcConsumer instance;

    private static Map<String, RpcConsumerHandler> handlerMap = new ConcurrentHashMap<>();

    private RpcConsumer(){
        localIP = IPUtils.getLocalHostIP();
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new RpcConsumerInitializer());
    }

    public static RpcConsumer getInstance(){
        if (instance == null){
            synchronized (RpcConsumer.class){
                if (instance == null){
                    instance = new RpcConsumer();
                }
            }
        }
        return instance;
    }

    public void close(){
        RpcConsumerHandlerHelper.closeRpcClientHandler();
        eventLoopGroup.shutdownGracefully();
        ClientThreadPool.shutdown();
    }

    @Override
    public RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception{
        RpcRequest request = protocol.getBody();
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        Object[] params = request.getParameters();
        int invokeHashcode = (params == null) || params.length <= 0 ? serviceKey.hashCode() : params[0].hashCode();
        ServiceMeta serviceMeta = registryService.discovery(serviceKey, invokeHashcode, localIP);
        if (serviceMeta != null){
            RpcConsumerHandler handler = RpcConsumerHandlerHelper.get(serviceMeta);
            //若缓存中没有handler
            if (handler == null){
                handler = getRpcConsumerHandler(serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
                RpcConsumerHandlerHelper.put(serviceMeta, handler);
            } else if (!handler.getChannel().isActive()){
                //缓存存在，但不活跃
                handler.close();
                handler = getRpcConsumerHandler(serviceMeta.getServiceAddr(),serviceMeta.getServicePort());
                RpcConsumerHandlerHelper.put(serviceMeta, handler);
            }
            return handler.sendRequest(protocol, request.isAsync(), request.isOneway());
        }
        return null;
    }

    /**
     * 创建连接并返回RpcConsumerHandler
     * @param serverAddress 地址
     * @param port 端口
     * @return RpcConsumerHandler
     * @throws InterruptedException e
     */
    private RpcConsumerHandler getRpcConsumerHandler(String serverAddress, int port) throws InterruptedException{
        ChannelFuture channelFuture = bootstrap.connect(serverAddress, port).sync();
        channelFuture.addListener((ChannelFutureListener) listener -> {
            if (channelFuture.isSuccess()){
                logger.info("connect rpc server {} on port {} success.", serverAddress, port);
            } else {
                logger.error("connect rpc server {} on port {} failed.", serverAddress, port);
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        return channelFuture.channel().pipeline().get(RpcConsumerHandler.class);
    }
}
