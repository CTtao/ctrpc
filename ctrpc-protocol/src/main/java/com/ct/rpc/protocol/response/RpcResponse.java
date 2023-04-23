package com.ct.rpc.protocol.response;

import com.ct.rpc.protocol.base.RpcMessage;

/**
 * @author CT
 * @version 1.0.0
 * @description RPC的响应类，对应的请求id在响应头中
 */
public class RpcResponse extends RpcMessage {
    private static final long serialVersionUID = 425335064405584525L;

    private String error;
    private Object result;

    public boolean isError(){
        return error != null;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
