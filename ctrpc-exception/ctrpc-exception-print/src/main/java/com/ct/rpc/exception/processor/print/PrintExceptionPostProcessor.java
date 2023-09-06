package com.ct.rpc.exception.processor.print;

import com.ct.rpc.exception.processor.ExceptionPostProcessor;
import com.ct.rpc.spi.annotation.SPIClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPIClass
public class PrintExceptionPostProcessor implements ExceptionPostProcessor {
    private final Logger logger = LoggerFactory.getLogger(PrintExceptionPostProcessor.class);

    @Override
    public void postExceptionProcessor(Throwable t) {
        logger.info("程序抛出了异常===>>>{}", t);
    }
}
