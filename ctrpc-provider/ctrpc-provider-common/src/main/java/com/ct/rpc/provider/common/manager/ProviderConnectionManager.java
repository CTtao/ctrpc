package com.ct.rpc.provider.common.manager;

import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.enumeration.RpcType;
import com.ct.rpc.protocol.header.RpcHeader;
import com.ct.rpc.protocol.header.RpcHeaderFactory;
import com.ct.rpc.protocol.response.RpcResponse;
import com.ct.rpc.provider.common.cache.ProviderChannelCache;
import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * @author CT
 * @version 1.0.0
 * @description 服务提供者连接管理器
 *
 */
public class ProviderConnectionManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProviderConnectionManager.class);
    /**
     * 扫描并移除不活跃的连接
     */
    public static void scanNotActiveChannel(){
        Set<Channel> channelCache = ProviderChannelCache.getChannelCache();
        if (channelCache == null || channelCache.isEmpty()) return;
        channelCache.stream().forEach((channel) -> {
            if (!channel.isOpen() || !channel.isActive()){
                channel.close();
                ProviderChannelCache.remove(channel);
            }
        });
    }

    /**
     * 发送ping消息
     */
    public static void broadcastPingMessageFromProvider(){
        Set<Channel> channelCache = ProviderChannelCache.getChannelCache();
        if (channelCache == null || channelCache.isEmpty()) return;
        RpcHeader header = RpcHeaderFactory.getRequestHeader(RpcConstants.SERIALIZATION_PROTOSTUFF, RpcType.HEARTBEAT_FROM_PROVIDER.getType());
        RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
        RpcResponse rpcResponse = new RpcResponse();
        rpcResponse.setResult(RpcConstants.HEARTBEAT_PING);
        responseRpcProtocol.setHeader(header);
        responseRpcProtocol.setBody(rpcResponse);
        channelCache.stream().forEach((channel) -> {
            if (channel.isOpen() && channel.isActive()){
                LOGGER.info("send heartbeat message to service consumer, the consumer is: {}, the heartbeat message is: {}", channel.remoteAddress(), rpcResponse.getResult());
                channel.writeAndFlush(responseRpcProtocol);
            }
        });
    }

}
