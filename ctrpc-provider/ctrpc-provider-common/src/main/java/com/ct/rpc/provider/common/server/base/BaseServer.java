package com.ct.rpc.provider.common.server.base;

import com.ct.rpc.codec.RpcDecoder;
import com.ct.rpc.codec.RpcEncoder;
import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.flow.processor.FlowPostProcessor;
import com.ct.rpc.provider.common.handler.RpcProviderHandler;
import com.ct.rpc.provider.common.manager.ProviderConnectionManager;
import com.ct.rpc.provider.common.server.api.Server;
import com.ct.rpc.registry.api.RegistryService;
import com.ct.rpc.registry.api.config.RegistryConfig;
import com.ct.rpc.registry.zookeeper.ZookeeperRegistryService;
import com.ct.rpc.spi.loader.ExtensionLoader;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author CT
 * @version 1.0.0
 * @description 基础服务
 */
public class BaseServer implements Server {
    private final Logger logger = LoggerFactory.getLogger(BaseServer.class);

    //主机域名或者IP地址
    protected String host = "127.0.0.1";

    //端口号
    protected int port = 27110;

    protected Map<String, Object> handlerMap = new HashMap<>();

    private String reflectType;

    protected RegistryService registryService;

    //心跳定时任务线程池
    private ScheduledExecutorService executorService;

    //心跳间隔时间，默认30s
    private int heartbeatInterval = 30000;

    private int scanNotActiveChannelInterval = 60000;

    //结果缓存过期时长，默认5秒
    private int resultCacheExpire = 5000;
    //是否开启结果缓存
    private boolean enableResultCache;

    //核心线程数
    private int corePoolSize;
    //最大线程数
    private int maxPoolSize;

    //流控分析后置处理器
    private FlowPostProcessor flowPostProcessor;

    //最大连接限制
    private int maxConnections;
    //拒绝策略类型
    private String disuseStrategyType;

    public BaseServer(String serverAddress,
                      String registryAddress,
                      String registryType,
                      String registryLoadBalanceType,
                      String reflectType,
                      int heartbeatInterval, int scanNotActiveChannelInterval,
                      boolean enableResultCache, int resultCacheExpire,
                      int corePoolSize, int maxPoolSize,
                      String flowType,
                      int maxConnections, String disuseStrategyType){
        if (!StringUtils.isEmpty(serverAddress)){
            String[] serverArray = serverAddress.split(":");
            this.host = serverArray[0];
            this.port = Integer.parseInt(serverArray[1]);
        }
        if (heartbeatInterval > 0){
            this.heartbeatInterval = heartbeatInterval;
        }
        if (scanNotActiveChannelInterval > 0){
            this.scanNotActiveChannelInterval = scanNotActiveChannelInterval;
        }
        this.reflectType = reflectType;
        this.registryService = this.getRegistryService(registryAddress, registryType, registryLoadBalanceType);
        if (resultCacheExpire > 0){
            this.resultCacheExpire = resultCacheExpire;
        }
        this.enableResultCache = enableResultCache;
        this.corePoolSize = corePoolSize;
        this.maxPoolSize = maxPoolSize;
        this.maxConnections = maxConnections;
        this.disuseStrategyType = disuseStrategyType;
        this.flowPostProcessor = ExtensionLoader.getExtension(FlowPostProcessor.class, flowType);
    }

    private RegistryService getRegistryService(String registryAddress, String registryType, String registryLoadBalanceType){
        RegistryService registryService = null;
        try {
            registryService = ExtensionLoader.getExtension(RegistryService.class, registryType);
            registryService.init(new RegistryConfig(registryAddress, registryType,registryLoadBalanceType));
        } catch (Exception e){
            logger.error("RPC server init error", e);
        }
        return registryService;
    }

    @Override
    public void startNettyServer() {
        this.startHeartbeat();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workGroup).channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel channel) throws Exception {
                            channel.pipeline()
                                    //todo 预留编解码，需要实现自定义协议
                                    .addLast(RpcConstants.CODEC_DECODER, new RpcDecoder(flowPostProcessor))
                                    .addLast(RpcConstants.CODEC_ENCODER, new RpcEncoder(flowPostProcessor))
                                    .addLast(RpcConstants.CODEC_SERVER_IDLE_HANDLER, new IdleStateHandler(0, 0, heartbeatInterval, TimeUnit.MILLISECONDS))
                                    .addLast(RpcConstants.CODEC_HANDLER, new RpcProviderHandler(reflectType,
                                            enableResultCache, resultCacheExpire,
                                            corePoolSize, maxPoolSize,
                                            maxConnections, disuseStrategyType,
                                            handlerMap));
                        }
                    })
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture future = bootstrap.bind(host, port).sync();
            logger.info("Server start on {}:{}", host, port);
            future.channel().closeFuture().sync();
        } catch (Exception e){
            logger.error("RPC server start error", e);
        } finally {
            workGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

    private void startHeartbeat() {
        executorService = Executors.newScheduledThreadPool(2);
        //扫描并处理所有不活跃的连接
        executorService.scheduleAtFixedRate(() -> {
            logger.info("=============scanNotActiveChannel============");
            ProviderConnectionManager.scanNotActiveChannel();
        }, 10, scanNotActiveChannelInterval, TimeUnit.MILLISECONDS);

        executorService.scheduleAtFixedRate(() -> {
            logger.info("=============broadcastPingMessageFromProvoder============");
            ProviderConnectionManager.broadcastPingMessageFromProvider();
        }, 3, heartbeatInterval, TimeUnit.MILLISECONDS);
    }
}
