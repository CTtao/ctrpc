package com.ct.rpc.consumer.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.ct.rpc.consumer.common.context.RpcContext;
import com.ct.rpc.protocol.enumeration.RpcType;
import com.ct.rpc.proxy.api.future.RpcFuture;
import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.header.RpcHeader;
import com.ct.rpc.protocol.request.RpcRequest;
import com.ct.rpc.protocol.response.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
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
    private Map<Long, RpcFuture> pendingRPC = new ConcurrentHashMap<>();

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
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
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
        logger.info("服务消费者接收到的数据===>>>{}", JSONObject.toJSONString(protocol));
        this.handlerMessage(protocol);
    }

    private void handlerMessage(RpcProtocol<RpcResponse> protocol){
        RpcHeader header = protocol.getHeader();
        //心跳消息
        if (header.getMsgType() == (byte) RpcType.HEARTBEAT.getType()){
            this.handlerHeartbeatMessage(protocol);
        } else if (header.getMsgType() == (byte) RpcType.RESPONSE.getType()){
            this.handlerResponseMessage(protocol, header);
        }
    }

    private void handlerHeartbeatMessage(RpcProtocol<RpcResponse> protocol){
        //简单打印
        logger.info("receive service provider heartbeat message: {}", protocol.getBody().getResult());
    }

    private void handlerResponseMessage(RpcProtocol<RpcResponse> protocol, RpcHeader header){
        long requestId = header.getRequestId();
        RpcFuture rpcFuture = pendingRPC.remove(requestId);
        if (rpcFuture != null){
            rpcFuture.done(protocol);
        }
    }

    public RpcFuture sendRequest(RpcProtocol<RpcRequest> protocol, boolean async, boolean oneway){
        logger.info("服务消费者发送的数据===>>>{}", JSONObject.toJSONString(protocol));
        return oneway ? this.sendRequestOneway(protocol) : async ? sendRequestAsync(protocol) : sendRequestSync(protocol);
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
        RpcFuture rpcFuture = new RpcFuture(protocol);
        RpcHeader header = protocol.getHeader();
        long requestId = header.getRequestId();
        pendingRPC.put(requestId, rpcFuture);
        return rpcFuture;
    }
    public void close(){
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }
}
