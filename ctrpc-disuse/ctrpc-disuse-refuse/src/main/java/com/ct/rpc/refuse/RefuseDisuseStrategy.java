package com.ct.rpc.refuse;

import com.ct.rpc.common.exception.RefuseException;
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
public class RefuseDisuseStrategy implements DisuseStrategy {
    private final Logger logger = LoggerFactory.getLogger(RefuseDisuseStrategy.class);

    @Override
    public ConnectionInfo selectConnection(List<ConnectionInfo> connectionList) {
        logger.info("execute refuse disuse strategy...");
        throw new RefuseException("refuse new connection...");
    }
}
