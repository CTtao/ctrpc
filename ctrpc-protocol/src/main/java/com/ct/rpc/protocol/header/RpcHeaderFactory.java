package com.ct.rpc.protocol.header;

import com.ct.rpc.common.id.IdFactory;
import com.ct.rpc.constants.RpcConstants;
import com.ct.rpc.protocol.enumeration.RpcType;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class RpcHeaderFactory {
    public static RpcHeader getRequestHeader(String serializationType, int messageType){
        RpcHeader header = new RpcHeader();
        Long requestId = IdFactory.getId();
        header.setMagic(RpcConstants.MAGIC);
        header.setRequestId(requestId);
        header.setMsgType((byte) messageType);
        header.setStatus((byte) 0x1);
        header.setSerializationType(serializationType);
        return header;
    }
}
