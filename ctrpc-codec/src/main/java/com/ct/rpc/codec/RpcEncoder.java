package com.ct.rpc.codec;

import com.ct.rpc.common.utils.SerializationUtils;
import com.ct.rpc.flow.processor.FlowPostProcessor;
import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.header.RpcHeader;
import com.ct.rpc.serialization.api.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import java.nio.charset.StandardCharsets;

/**
 * @author CT
 * @version 1.0.0
 * @description 实现RPC编码
 */
public class RpcEncoder extends MessageToByteEncoder<RpcProtocol<Object>> implements RpcCodec{
    private FlowPostProcessor postProcessor;

    public RpcEncoder(FlowPostProcessor postProcessor){
        this.postProcessor = postProcessor;
    }
    @Override
    protected void encode(ChannelHandlerContext ctx, RpcProtocol<Object> msg, ByteBuf byteBuf) throws Exception {
        RpcHeader header = msg.getHeader();
        byteBuf.writeShort(header.getMagic());
        byteBuf.writeByte(header.getMsgType());
        byteBuf.writeByte(header.getStatus());
        byteBuf.writeLong(header.getRequestId());
        String serializationType = header.getSerializationType();
        Serialization serialization = getJdkSerialization(serializationType);
        byteBuf.writeBytes(SerializationUtils.paddingString(serializationType).getBytes(StandardCharsets.UTF_8));
        byte[] data = serialization.serialize(msg.getBody());
        byteBuf.writeInt(data.length);
        byteBuf.writeBytes(data);
        //异步调用流控分析后置处理器
        header.setMsgLen(data.length);
        this.postFlowProcessor(postProcessor, header);
    }
}
