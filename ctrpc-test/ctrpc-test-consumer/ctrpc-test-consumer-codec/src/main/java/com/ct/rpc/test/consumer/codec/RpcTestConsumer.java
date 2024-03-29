package com.ct.rpc.test.consumer.codec;

import com.ct.rpc.flow.processor.FlowPostProcessor;
import com.ct.rpc.spi.loader.ExtensionLoader;
import com.ct.rpc.test.consumer.codec.init.RpcTestConsumerInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author CT
 * @version 1.0.0
 * @description 服务消费者
 */
public class RpcTestConsumer {
    public static void main(String[] args) throws InterruptedException{
        Bootstrap bootstrap = new Bootstrap();
        EventLoopGroup eventLoopGroup =  new NioEventLoopGroup(4);
        try {
            bootstrap.group(eventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new RpcTestConsumerInitializer(ExtensionLoader.getExtension(FlowPostProcessor.class, "print")));
            bootstrap.connect("127.0.0.1",27880).sync();
        } catch (Exception e){
            e.printStackTrace();
        } finally {
            Thread.sleep(2000);
            eventLoopGroup.shutdownGracefully();
        }
    }
}
