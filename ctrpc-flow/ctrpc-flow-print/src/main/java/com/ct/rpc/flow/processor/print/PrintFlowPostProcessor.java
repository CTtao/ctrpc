package com.ct.rpc.flow.processor.print;

import com.ct.rpc.flow.processor.FlowPostProcessor;
import com.ct.rpc.protocol.header.RpcHeader;
import com.ct.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPIClass
public class PrintFlowPostProcessor implements FlowPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(PrintFlowPostProcessor.class);

    @Override
    public void postRpcHeaderProcessor(RpcHeader rpcHeader) {
        logger.info(getRpcHeaderString(rpcHeader));
    }

    private String getRpcHeaderString(RpcHeader rpcHeader){
        StringBuilder sb = new StringBuilder();
        sb.append("magic: " + rpcHeader.getMagic());
        sb.append(", requestId: " + rpcHeader.getRequestId());
        sb.append(", msgType: " + rpcHeader.getMsgType());
        sb.append(", serializationType: " + rpcHeader.getSerializationType());
        sb.append(", status: " + rpcHeader.getStatus());
        sb.append(", msgLen: " + rpcHeader.getMsgLen());

        return sb.toString();
    }
}
