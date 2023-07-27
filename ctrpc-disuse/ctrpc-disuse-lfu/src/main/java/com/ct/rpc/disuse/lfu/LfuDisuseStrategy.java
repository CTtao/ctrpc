package com.ct.rpc.disuse.lfu;

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
public class LfuDisuseStrategy implements DisuseStrategy {
    private final Logger logger = LoggerFactory.getLogger(LfuDisuseStrategy.class);
    private final Comparator<ConnectionInfo> useCountComparator = (o1, o2) -> {
        return o1.getUseCount() - o2.getUseCount() > 0 ? 1 : -1;
    };
    @Override
    public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
        logger.info("execute lfu disuse strategy...");
        if (connectionList.isEmpty()) return null;
        Collections.sort(connectionList, useCountComparator);
        return connectionList.get(0);
    }
}
