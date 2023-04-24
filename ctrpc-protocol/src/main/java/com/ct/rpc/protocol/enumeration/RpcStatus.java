package com.ct.rpc.protocol.enumeration;

/**
 * @author CT
 * @version 1.0.0
 * @description 服务调用状态
 */
public enum RpcStatus {
    SUCCESS(0),
    FAIL(1)
    ;

    private final int code;

    RpcStatus(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
