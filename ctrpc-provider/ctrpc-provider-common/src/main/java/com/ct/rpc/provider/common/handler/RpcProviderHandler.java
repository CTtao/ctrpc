package com.ct.rpc.provider.common.handler;

import com.alibaba.fastjson.JSONObject;
import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.enumeration.RpcType;
import com.ct.rpc.protocol.header.RpcHeader;
import com.ct.rpc.protocol.request.RpcRequest;
import com.ct.rpc.protocol.response.RpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * @author CT
 * @version 1.0.0
 * @description RPC服务提供者的Handler处理类
 */
public class RpcProviderHandler extends SimpleChannelInboundHandler<RpcProtocol<RpcRequest>> {
    private final Logger logger = LoggerFactory.getLogger(RpcProviderHandler.class);

    /**
     * 存储服务提供者中被@RpcService注解标注的类的对象
     * key为：serviceName#serviceVersion#group
     * value为：@RpcService注解标注的类的对象
     */
    private final Map<String, Object> handlerMap;

    public RpcProviderHandler(Map<String, Object> handlerMap){
        this.handlerMap = handlerMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcProtocol<RpcRequest> protocol) throws Exception {
        logger.info("Rpc提供者收到的数据为===>>> " + JSONObject.toJSONString(protocol));
        logger.info("handlerMap存放的数据如下所示：");
        for (Map.Entry<String, Object> entry : handlerMap.entrySet()) {
            logger.info(entry.getKey() + " === " + entry.getValue());
        }
        RpcHeader header = protocol.getHeader();
        RpcRequest request = protocol.getBody();
        //将header的消息类型设置为响应类型
        header.setMsgType((byte) RpcType.RESPONSE.getType());
        //构建响应协议数据
        RpcProtocol<RpcResponse> responseRpcProtocol = new RpcProtocol<>();
        RpcResponse response = new RpcResponse();
        response.setResult("数据交互成功");
        response.setOneway(request.isOneway());
        response.setAsync(request.isAsync());
        responseRpcProtocol.setHeader(header);
        responseRpcProtocol.setBody(response);

        ctx.writeAndFlush(responseRpcProtocol);
    }
}
