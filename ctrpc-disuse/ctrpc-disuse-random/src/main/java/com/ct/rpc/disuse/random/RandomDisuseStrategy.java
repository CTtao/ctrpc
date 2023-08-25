package com.ct.rpc.disuse.random;

import com.ct.rpc.disuse.api.DisuseStrategy;
import com.ct.rpc.disuse.api.connection.ConnectionInfo;
import com.ct.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Random;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPIClass
public class RandomDisuseStrategy implements DisuseStrategy {
    private final Logger logger = LoggerFactory.getLogger(RandomDisuseStrategy.class);

    @Override
    public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
        logger.info("execute random disuse strategy...");
        if (connectionList.isEmpty()) return null;
        return connectionList.get(new Random().nextInt(connectionList.size()));
    }
}
