package com.ct.rpc.flow.processor;

import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.protocol.header.RpcHeader;
import com.ct.rpc.spi.annotation.SPI;

/**
 * @author CT
 * @version 1.0.0
 * @description 流量分析后置处理器接口
 */
@SPI(RpcConstants.FLOW_POST_PROCESSOR_PRINT)
public interface FlowPostProcessor {
    /**
     * 流控分析后置处理器方法
     */
    void postRpcHeaderProcessor(RpcHeader rpcHeader);
}
