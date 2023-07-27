package com.ct.rpc.disuse.fifo;

import com.ct.rpc.disuse.api.DisuseStrategy;
import com.ct.rpc.disuse.api.connection.ConnectionInfo;
import com.ct.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPIClass
public class FifoDisuseStrategy implements DisuseStrategy {
    private final Logger logger = LoggerFactory.getLogger(FifoDisuseStrategy.class);
    private final Comparator<ConnectionInfo> connectionInfoComparator = (o1, o2) -> {
      return o1.getConnectionTime() - o2.getConnectionTime() > 0 ? 1 : -1;
    };

    @Override
    public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
        logger.info("execute fifo disuse strategy...");
        if (connectionList.isEmpty()) return null;
        Collections.sort(connectionList, connectionInfoComparator);
        return connectionList.get(0);
    }
}
