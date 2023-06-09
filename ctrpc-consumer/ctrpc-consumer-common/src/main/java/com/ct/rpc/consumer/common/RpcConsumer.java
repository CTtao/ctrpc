package com.ct.rpc.consumer.common;

import com.ct.rpc.common.helper.RpcServiceHelper;
import com.ct.rpc.common.ip.IPUtils;
import com.ct.rpc.common.threadpool.ClientThreadPool;
import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.consumer.common.helper.RpcConsumerHandlerHelper;
import com.ct.rpc.consumer.common.manager.ConsumerConnectionManager;
import com.ct.rpc.loadbalancer.context.ConnectionsContext;
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
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

    private ScheduledExecutorService executorService;

    //心跳间隔时间，默认30s
    private int heartbeatInterval = 30000;

    //扫描并移除连接时间，默认60s
    private int scanNotActiveChannelInterval = 60000;

    //重试间隔时间
    private int retryInterval = 1000;

    //重新次数
    private int retryTimes = 3;

    //当前重试次数
    private volatile int currentConnectRetryTimes = 0;

    private RpcConsumer(int heartbeatInterval, int scanNotActiveChannelInterval, int retryInterval, int retryTimes){
        if (heartbeatInterval > 0){
            this.heartbeatInterval = heartbeatInterval;
        }
        if (scanNotActiveChannelInterval > 0){
            this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
        }
        this.retryInterval = retryInterval <= 0 ? RpcConstants.DEFAULT_RETRY_INTERVAL : retryInterval;
        this.retryTimes = retryTimes <= 0 ? RpcConstants.DEFAULT_RETRY_TIMES : retryTimes;
        localIP = IPUtils.getLocalHostIP();
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new RpcConsumerInitializer(heartbeatInterval));
        //todo 启动心跳
        this.startHeartbeat();
    }

    private void startHeartbeat(){
        executorService = Executors.newScheduledThreadPool(2);
        //扫描并处理所有不活跃的连接
        executorService.scheduleAtFixedRate(() -> {
            logger.info("=============scanNotActiveChannel============");
            ConsumerConnectionManager.scanNotActiveChannel();
        }, 10, scanNotActiveChannelInterval, TimeUnit.MILLISECONDS);

        executorService.scheduleAtFixedRate(() -> {
            logger.info("=============broadcastPingMessageFromConsumer============");
            ConsumerConnectionManager.broadcastPingMessageFromConsumer();
        }, 3, heartbeatInterval, TimeUnit.MILLISECONDS);
    }

    public static RpcConsumer getInstance(int heartbeatInterval, int scanNotActiveChannelInterval, int retryInterval, int retryTimes){
        if (instance == null){
            synchronized (RpcConsumer.class){
                if (instance == null){
                    instance = new RpcConsumer(heartbeatInterval, scanNotActiveChannelInterval, retryInterval, retryTimes);
                }
            }
        }
        return instance;
    }

    public void close(){
        RpcConsumerHandlerHelper.closeRpcClientHandler();
        eventLoopGroup.shutdownGracefully();
        ClientThreadPool.shutdown();
        executorService.shutdown();
    }

    @Override
    public RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol, RegistryService registryService) throws Exception{
        RpcRequest request = protocol.getBody();
        String serviceKey = RpcServiceHelper.buildServiceKey(request.getClassName(), request.getVersion(), request.getGroup());
        Object[] params = request.getParameters();
        int invokeHashcode = (params == null) || params.length <= 0 ? serviceKey.hashCode() : params[0].hashCode();
        ServiceMeta serviceMeta = this.getServiceMetaWithRetry(registryService, serviceKey, invokeHashcode);
        RpcConsumerHandler handler = null;
        if (serviceMeta != null){
            handler = getRpcConsumerHandlerWithRetry(serviceMeta);
        }
        RpcFuture rpcFuture = null;
        if (handler != null){
            rpcFuture = handler.sendRequest(protocol, request.isAsync(), request.isOneway());
        }
        return rpcFuture;
    }

    /**
     * 重试并获取服务提供者元数据
     */
    private ServiceMeta getServiceMetaWithRetry(RegistryService registryService, String serviceKey, int invokeHashCode) throws Exception{
        //首次获取服务元数据信息，如果获取到，则直接返回，否则进行重试
        logger.info("获取服务提供者元数据...");
        ServiceMeta serviceMeta = registryService.discovery(serviceKey, invokeHashCode, localIP);
        //启动重试机制
        if (serviceMeta == null){
            for (int i = 1; i <= retryTimes; i++){
                logger.info("获取服务提供者元数据第【{}】次重试...", i);
                serviceMeta = registryService.discovery(serviceKey, invokeHashCode, localIP);
                if (serviceMeta != null){
                    break;
                }
                Thread.sleep(retryInterval);
            }
        }
        return serviceMeta;
    }

    /**
     * 获取RpcConsumerHandler
     */
    private RpcConsumerHandler getRpcConsumerHandlerWithRetry(ServiceMeta serviceMeta) throws InterruptedException{
        logger.info("服务消费者连接服务提供者...");
        RpcConsumerHandler handler = null;
        try {
            handler = this.getRpcConsumerHandlerWithCache(serviceMeta);
        } catch (Exception e){
            //连接异常
            if (handler == null){
                if (currentConnectRetryTimes < retryTimes){
                    currentConnectRetryTimes++;
                    logger.info("服务消费者连接服务提供者第【{}】次重试...", currentConnectRetryTimes);
                    handler = this.getRpcConsumerHandlerWithRetry(serviceMeta);
                    Thread.sleep(retryInterval);
                }
            }
        }
        return handler;
    }

    /**
     * 从缓存中获取RpcConsumerHandler,缓存总没有则创建
     */
    private RpcConsumerHandler getRpcConsumerHandlerWithCache(ServiceMeta serviceMeta) throws InterruptedException{
        RpcConsumerHandler handler = RpcConsumerHandlerHelper.get(serviceMeta);
        //若缓存中没有handler
        if (handler == null){
            handler = getRpcConsumerHandler(serviceMeta);
            RpcConsumerHandlerHelper.put(serviceMeta, handler);
        } else if (!handler.getChannel().isActive()){
            //缓存存在，但不活跃
            handler.close();
            handler = getRpcConsumerHandler(serviceMeta);
            RpcConsumerHandlerHelper.put(serviceMeta, handler);
        }
        return handler;
    }

    /**
     * 创建连接并返回RpcConsumerHandler
     * @param serviceMeta 元数据
     * @return RpcConsumerHandler
     * @throws InterruptedException e
     */
    private RpcConsumerHandler getRpcConsumerHandler(ServiceMeta serviceMeta) throws InterruptedException{
        ChannelFuture channelFuture = bootstrap.connect(serviceMeta.getServiceAddr(), serviceMeta.getServicePort()).sync();
        channelFuture.addListener((ChannelFutureListener) listener -> {
            if (channelFuture.isSuccess()){
                logger.info("connect rpc server {} on port {} success.", serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
                //添加连接信息，在服务消费者端记录每个服务提供者实例的连接次数
                ConnectionsContext.add(serviceMeta);
                //连接成功，将当前连接重试次数设置为0
                currentConnectRetryTimes = 0;
            } else {
                logger.error("connect rpc server {} on port {} failed.", serviceMeta.getServiceAddr(), serviceMeta.getServicePort());
                channelFuture.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        return channelFuture.channel().pipeline().get(RpcConsumerHandler.class);
    }
}
