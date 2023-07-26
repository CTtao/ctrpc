package com.ct.rpc.disuse.defautstrategy;

import com.ct.rpc.disuse.api.DisuseStrategy;
import com.ct.rpc.disuse.api.connection.ConnectionInfo;
import com.ct.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author CT
 * @version 1.0.0
 * @description 默认连接策略，获取列表中的第一个元素
 */
@SPIClass
public class DefaultDisuseStrategy implements DisuseStrategy {
    private final Logger logger = LoggerFactory.getLogger(DefaultDisuseStrategy.class);

    @Override
    public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
        logger.info("execute default disuse strategy...");
        return connectionList.get(0);
    }
}
