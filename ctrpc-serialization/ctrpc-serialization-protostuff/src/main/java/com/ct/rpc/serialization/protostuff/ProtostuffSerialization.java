package com.ct.rpc.serialization.protostuff;

import com.ct.rpc.common.exception.SerializerException;
import com.ct.rpc.serialization.api.Serialization;
import com.ct.rpc.spi.annotation.SPIClass;
import com.dyuproject.protostuff.LinkedBuffer;
import com.dyuproject.protostuff.ProtostuffIOUtil;
import com.dyuproject.protostuff.Schema;
import com.dyuproject.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author CT
 * @version 1.0.0
 * @description Protostuff Serialization
 */
@SPIClass
public class ProtostuffSerialization implements Serialization {
    private final Logger logger = LoggerFactory.getLogger(ProtostuffSerialization.class);

    private Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private Objenesis objenesis = new ObjenesisStd(true);

    @SuppressWarnings("unchecked")
    private <T> Schema<T> getSchema(Class<T> cls){
        Schema<T> schema = (Schema<T>) cachedSchema.get(cls);
        if (schema == null){
            schema = RuntimeSchema.createFrom(cls);
            if (schema != null){
                cachedSchema.put(cls, schema);
            }
        }
        return schema;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> byte[] serialize(T obj) {
        logger.info("execute protostuff serialize...");
        if (obj == null){
            throw new SerializerException("serialize object is null");
        }
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e){
            throw new SerializerException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        logger.info("execute protostuff deserialize...");
        if (data == null){
            throw new SerializerException("deserialize data is null");
        }
        try {
            T message = objenesis.newInstance(cls);
            Schema<T> schema = getSchema(cls);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e){
            throw new SerializerException(e.getMessage(), e);
        }
    }
}
