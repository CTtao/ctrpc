package com.ct.rpc.consumer.common.manager;

import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.consumer.common.cache.ConsumerChannelCache;
import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.enumeration.RpcType;
import com.ct.rpc.protocol.header.RpcHeader;
import com.ct.rpc.protocol.header.RpcHeaderFactory;
import com.ct.rpc.protocol.request.RpcRequest;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class ConsumerConnectionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ConsumerConnectionManager.class);

    /**
     * 扫描不活跃的连接
     */
    public static void scanNotActiveChannel(){
        Set<Channel> channelCache = ConsumerChannelCache.getChannelCache();
        if (channelCache == null || channelCache.isEmpty()){
            return;
        }
        channelCache.stream().forEach(channel -> {
            if (!channel.isOpen() || !channel.isActive()){
                channel.close();
                ConsumerChannelCache.remove(channel);
            }
        });
    }

    /**
     * 发送ping消息
     */
    public static void broadcastPingMessageFromConsumer(){
        Set<Channel> channelCache = ConsumerChannelCache.getChannelCache();
        if (channelCache == null || channelCache.isEmpty()){
            return;
        }
        RpcHeader header = RpcHeaderFactory.getRequestHeader(RpcConstants.SERIALIZATION_PROTOSTUFF, RpcType.HEARTBEAT_FROM_CONSUMER.getType());
        RpcProtocol<RpcRequest> requestRpcProtocol = new RpcProtocol<>();
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setParameters(new Object[]{RpcConstants.HEARTBEAT_PING});
        requestRpcProtocol.setHeader(header);
        requestRpcProtocol.setBody(rpcRequest);
        channelCache.stream().forEach(channel -> {
            if (channel.isOpen() && channel.isActive()){
                LOGGER.info("send heartbeat message to service provider, the provider is: {}, the heartbeat message is: {}"
                        , channel.remoteAddress()
                        , RpcConstants.HEARTBEAT_PING);
                channel.writeAndFlush(requestRpcProtocol);
            }
        });
    }
}
