package com.ct.rpc.consumer.common.initializer;

import com.ct.rpc.codec.RpcDecoder;
import com.ct.rpc.codec.RpcEncoder;
import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.consumer.common.handler.RpcConsumerHandler;
import com.ct.rpc.threadpool.ConcurrentThreadPool;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class RpcConsumerInitializer extends ChannelInitializer<SocketChannel> {

    private int heartbeatInterval;

    private ConcurrentThreadPool concurrentThreadPool;

    public RpcConsumerInitializer(int heartbeatInterval, ConcurrentThreadPool concurrentThreadPool) {
        if (heartbeatInterval > 0){
            this.heartbeatInterval = heartbeatInterval;
        }
        this.concurrentThreadPool = concurrentThreadPool;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline cp = channel.pipeline();
        cp.addLast(RpcConstants.CODEC_ENCODER, new RpcEncoder());
        cp.addLast(RpcConstants.CODEC_DECODER, new RpcDecoder());
        cp.addLast(RpcConstants.CODEC_CLIENT_IDLE_HANDLER, new IdleStateHandler(heartbeatInterval, 0, 0, TimeUnit.MILLISECONDS));
        cp.addLast(RpcConstants.CODEC_HANDLER, new RpcConsumerHandler(concurrentThreadPool));
    }
}
