package com.ct.rpc.codec;

import com.ct.rpc.serialization.api.Serialization;
import com.ct.rpc.serialization.jdk.JdkSerialization;

/**
 * @author CT
 * @version 1.0.0
 * @description 实现编解码的接口，提供序列化和反序列化的默认方法
 */
public interface RpcCodec {

    default Serialization getJdkSerialization(){
        return new JdkSerialization();
    }
}
