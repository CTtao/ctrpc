package com.ct.rpc.consumer.common;

import com.ct.rpc.common.threadpool.ClientThreadPool;
import com.ct.rpc.consumer.common.future.RpcFuture;
import com.ct.rpc.consumer.common.handler.RpcConsumerHandler;
import com.ct.rpc.consumer.common.initializer.RpcConsumerInitializer;
import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.request.RpcRequest;
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
public class RpcConsumer {
    private final Logger logger = LoggerFactory.getLogger(RpcConsumer.class);
    private final Bootstrap bootstrap;
    private final EventLoopGroup eventLoopGroup;

    private static volatile RpcConsumer instance;

    private static Map<String, RpcConsumerHandler> handlerMap = new ConcurrentHashMap<>();

    private RpcConsumer(){
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
        eventLoopGroup.shutdownGracefully();
        ClientThreadPool.shutdown();
    }

    public RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol) throws Exception{
        //todo 暂时写死，待引入注册中心
        String serverAddress = "127.0.0.1";
        int port = 27880;
        String key = serverAddress.concat("_").concat(String.valueOf(port));
        RpcConsumerHandler handler = handlerMap.get(key);
        if (handler == null){
            handler = getRpcConsumerHandler(serverAddress, port);
            handlerMap.put(key, handler);
        } else if (!handler.getChannel().isActive()){
            //缓存中存在RpcConsumerHandler，但不活跃
            handler.close();
            handler = getRpcConsumerHandler(serverAddress, port);
            handlerMap.put(key, handler);
        }
        RpcRequest request = protocol.getBody();
        return handler.sendRequest(protocol, request.isAsync(), request.isOneway());
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
