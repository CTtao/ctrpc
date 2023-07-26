package com.ct.rpc.disuse.last;

import com.ct.rpc.disuse.api.DisuseStrategy;
import com.ct.rpc.disuse.api.connection.ConnectionInfo;
import com.ct.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPIClass
public class LastDisuseStrategy implements DisuseStrategy {
    private static final Logger logger = LoggerFactory.getLogger(LastDisuseStrategy.class);

    @Override
    public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
        logger.info("execute last disuse strategy...");
        return connectionList.get(connectionList.size() - 1);
    }
}
