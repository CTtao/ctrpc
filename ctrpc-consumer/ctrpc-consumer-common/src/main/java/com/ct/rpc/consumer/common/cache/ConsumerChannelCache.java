package com.ct.rpc.consumer.common.cache;

import io.netty.channel.Channel;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author CT
 * @version 1.0.0
 * @description 缓存成功连接
 */
public class ConsumerChannelCache {
    private static volatile Set<Channel> channelCache = new CopyOnWriteArraySet<>();

    public static void add(Channel channel){
        channelCache.add(channel);
    }

    public static void remove(Channel channel){
        channelCache.remove(channel);
    }

    public static Set<Channel> getChannelCache(){
        return channelCache;
    }
}
