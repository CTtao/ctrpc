package com.ct.rpc.disuse.api;

import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.disuse.api.connection.ConnectionInfo;
import com.ct.rpc.spi.annotation.SPI;

import java.util.List;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPI(RpcConstants.RPC_CONNECTION_DISUSE_STRATEGY_DEFAULT)
public interface DisuseStrategy {
    /**
     * 从连接列表中根据规则获取一个连接对象
     */
    ConnectionInfo selectConnection(List<ConnectionInfo> connectionList);
}
