package com.ct.rpc.consumer.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.ct.rpc.buffer.cache.BufferCacheManager;
import com.ct.rpc.buffer.object.BufferObject;
import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.consumer.common.cache.ConsumerChannelCache;
import com.ct.rpc.consumer.common.context.RpcContext;
import com.ct.rpc.exception.processor.ExceptionPostProcessor;
import com.ct.rpc.protocol.enumeration.RpcStatus;
import com.ct.rpc.protocol.enumeration.RpcType;
import com.ct.rpc.protocol.header.RpcHeaderFactory;
import com.ct.rpc.proxy.api.future.RpcFuture;
import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.header.RpcHeader;
import com.ct.rpc.protocol.request.RpcRequest;
import com.ct.rpc.protocol.response.RpcResponse;
import com.ct.rpc.threadpool.BufferCacheThreadPool;
import com.ct.rpc.threadpool.ConcurrentThreadPool;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class RpcConsumerHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcResponse>> {
    private final Logger logger = LoggerFactory.getLogger(RpcConsumerHandler.class);
    private volatile Channel channel;
    private SocketAddress remotePeer;

    //请求ID与RpcResponse协议的映射关系
    private static final Map<Long, RpcFuture> pendingRPC = new ConcurrentHashMap<>();

    private ConcurrentThreadPool concurrentThreadPool;

    private boolean enableBuffer;

    private BufferCacheManager<RpcProtocol<RpcResponse>> bufferCacheManager;

    /**
     * 异常后置处理器
     */
    private ExceptionPostProcessor exceptionPostProcessor;

    public RpcConsumerHandler(boolean enableBuffer, int bufferSize, ConcurrentThreadPool concurrentThreadPool, ExceptionPostProcessor exceptionPostProcessor){
        this.concurrentThreadPool = concurrentThreadPool;
        this.exceptionPostProcessor = exceptionPostProcessor;
        this.enableBuffer = enableBuffer;
        if (enableBuffer){
            this.bufferCacheManager = BufferCacheManager.getInstance(bufferSize);
            BufferCacheThreadPool.submit(() -> {
                consumeBufferCache();
            });
        }
    }

    /**
     * 消费缓冲区数据
     */
    private void consumeBufferCache(){
        //不断消费缓冲区的数据
        while (true){
            RpcProtocol<RpcResponse> protocol = this.bufferCacheManager.take();
            if (protocol != null){
                this.handlerResponseMessage(protocol);
            }
        }
    }

    public Channel getChannel() {
        return channel;
    }

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
        ConsumerChannelCache.add(channel);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent){
            RpcHeader header = RpcHeaderFactory.getRequestHeader(RpcConstants.SERIALIZATION_PROTOSTUFF, RpcType.HEARTBEAT_FROM_CONSUMER.getType());
            RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
            RpcRequest rpcRequest = new RpcRequest();
            rpcRequest.setParameters(new Object[]{RpcConstants.HEARTBEAT_PING});
            requestRpcProtocol.setHeader(header);
            requestRpcProtocol.setBody(rpcRequest);
            ctx.writeAndFlush(requestRpcProtocol);
        }else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        super.channelUnregistered(ctx);
        ConsumerChannelCache.remove(ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        ConsumerChannelCache.remove(ctx.channel());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcProtocol<RpcResponse> protocol) throws Exception {
        if (protocol == null){
            return;
        }
        concurrentThreadPool.submit(() -> {
            this.handlerMessage(protocol, channelHandlerContext.channel());
        });
    }

    private void handlerMessage(RpcProtocol<RpcResponse> protocol, Channel channel){
        RpcHeader header = protocol.getHeader();
        //心跳消息
        if (header.getMsgType() == (byte) RpcType.HEARTBEAT_TO_CONSUMER.getType()){
            this.handlerHeartbeatMessageToConsumer(protocol, channel);
        } else if (header.getMsgType() == (byte) RpcType.HEARTBEAT_FROM_PROVIDER.getType()){
            this.handlerHeartbeatMessageFromProvider(protocol, channel);
        }else if (header.getMsgType() == (byte) RpcType.RESPONSE.getType()){
            this.handlerResponseMessageOrBuffer(protocol);
        }
    }

    /**
     * 包含是否开启了缓冲区的响应信息
     */
    private void handlerResponseMessageOrBuffer(RpcProtocol<RpcResponse> protocol){
        if (enableBuffer){
            logger.info("enable buffer...");
            this.bufferCacheManager.put(protocol);
        }else {
            this.handlerResponseMessage(protocol);
        }
    }

    /**
     * 处理从服务提供者发送过来的心跳消息
     */
    private void handlerHeartbeatMessageFromProvider(RpcProtocol<RpcResponse> protocol, Channel channel) {
        RpcHeader header = protocol.getHeader();
        header.setMsgType((byte) RpcType.HEARTBEAT_TO_PROVIDER.getType());
        RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
        RpcRequest request = new RpcRequest();
        request.setParameters(new Object[]{RpcConstants.HEARTBEAT_PONG});
        header.setStatus((byte) RpcStatus.SUCCESS.getCode());
        requestRpcProtocol.setHeader(header);
        requestRpcProtocol.setBody(request);
        channel.writeAndFlush(requestRpcProtocol);
    }


    /**
     * 处理心跳信息
     */
    private void handlerHeartbeatMessageToConsumer(RpcProtocol<RpcResponse> protocol, Channel channel){
        //简单打印
        logger.info("receive service provider heartbeat message, the provider is: {}, the heartbeat message is: {}", channel.remoteAddress(), protocol.getBody().getResult());
    }

    private void handlerResponseMessage(RpcProtocol<RpcResponse> protocol){
        long requestId = protocol.getHeader().getRequestId();
        RpcFuture rpcFuture = pendingRPC.remove(requestId);
        if (rpcFuture != null){
            rpcFuture.done(protocol);
        }
    }

    public RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol, boolean async, boolean oneway){
        logger.info("服务消费者发送的数据===>>>{}", JSONObject.toJSONString(protocol));
        return concurrentThreadPool.submit(() -> {
            return oneway ? this.sendRequestOneway(protocol) : async ? sendRequestAsync(protocol) : this.sendRequestSync(protocol);
        });
    }

    private RpcFuture sendRequestSync(RpcProtocol<RpcRequest> protocol){
        RpcFuture rpcFuture = this.getRpcFuture(protocol);
        channel.writeAndFlush(protocol);
        return rpcFuture;
    }

    private RpcFuture sendRequestAsync(RpcProtocol<RpcRequest> protocol){
        RpcFuture rpcFuture = this.getRpcFuture(protocol);
        //异步调用
        RpcContext.getContext().setRpcFuture(rpcFuture);
        channel.writeAndFlush(protocol);
        return null;
    }

    private RpcFuture sendRequestOneway(RpcProtocol<RpcRequest> protocol){
        channel.writeAndFlush(protocol);
        return null;
    }

    private RpcFuture getRpcFuture(RpcProtocol<RpcRequest> protocol){
        RpcFuture rpcFuture = new RpcFuture(protocol, concurrentThreadPool);
        RpcHeader header = protocol.getHeader();
        long requestId = header.getRequestId();
        pendingRPC.put(requestId, rpcFuture);
        return rpcFuture;
    }
    public void close(){
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        ConsumerChannelCache.remove(channel);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        exceptionPostProcessor.postExceptionProcessor(cause);
        super.exceptionCaught(ctx, cause);
    }
}
