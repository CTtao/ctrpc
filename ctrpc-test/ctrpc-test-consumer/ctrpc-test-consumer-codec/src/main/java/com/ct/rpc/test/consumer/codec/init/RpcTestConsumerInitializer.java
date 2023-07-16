package com.ct.rpc.test.consumer.codec.init;

import com.ct.rpc.codec.RpcDecoder;
import com.ct.rpc.codec.RpcEncoder;
import com.ct.rpc.flow.processor.FlowPostProcessor;
import com.ct.rpc.test.consumer.codec.handler.RpcTestConsumerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;

/**
 * @author CT
 * @version 1.0.0
 * @description netty处理链初始化
 */
public class RpcTestConsumerInitializer extends ChannelInitializer<SocketChannel> {
    private FlowPostProcessor flowPostProcessor;

    public RpcTestConsumerInitializer(FlowPostProcessor flowPostProcessor){
        this.flowPostProcessor = flowPostProcessor;
    }
    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline cp = socketChannel.pipeline();
        cp.addLast(new RpcEncoder(flowPostProcessor));
        cp.addLast(new RpcDecoder(flowPostProcessor));
        cp.addLast(new RpcTestConsumerHandler());
    }
}
