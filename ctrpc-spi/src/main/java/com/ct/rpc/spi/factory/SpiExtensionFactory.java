package com.ct.rpc.spi.factory;

import com.ct.rpc.spi.annotation.SPI;
import com.ct.rpc.spi.annotation.SPIClass;
import com.ct.rpc.spi.loader.ExtensionLoader;

import java.util.Optional;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
@SPIClass
public class SpiExtensionFactory implements ExtensionFactory{
    @Override
    public <T> T getExtension(String key, Class<T> clazz) {
        return Optional.ofNullable(clazz)
                .filter(Class::isInterface)
                .filter(cls -> cls.isAnnotationPresent(SPI.class))
                .map(ExtensionLoader::getExtensionLoader)
                .map(ExtensionLoader::getDefaultSpiClassInstance)
                .orElse(null);
    }
}
