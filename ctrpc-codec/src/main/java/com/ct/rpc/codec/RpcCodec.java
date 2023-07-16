package com.ct.rpc.codec;

import com.ct.rpc.flow.processor.FlowPostProcessor;
import com.ct.rpc.protocol.header.RpcHeader;
import com.ct.rpc.serialization.api.Serialization;
import com.ct.rpc.serialization.jdk.JdkSerialization;
import com.ct.rpc.spi.loader.ExtensionLoader;
import com.ct.rpc.threadpool.FlowPostProcessorThreadPool;

/**
 * @author CT
 * @version 1.0.0
 * @description 实现编解码的接口，提供序列化和反序列化的默认方法
 */
public interface RpcCodec {
    /**
     * 根据serializationType通过SPI获取序列化句柄
     * @param serializationType 序列化方式
     * @return Serialization对象
     */
    default Serialization getJdkSerialization(String serializationType){
        return ExtensionLoader.getExtension(Serialization.class, serializationType);
    }

    /**
     * 调用RPC框架流量分析后置处理器
     * @param postProcessor 后置处理器
     * @param header 封装了流量信息的消息头
     */
    default void postFlowProcessor(FlowPostProcessor postProcessor, RpcHeader header){
        //异步调用流控分析后置处理器
        FlowPostProcessorThreadPool.submit(() -> {
            postProcessor.postRpcHeaderProcessor(header);
        });
    }
}
