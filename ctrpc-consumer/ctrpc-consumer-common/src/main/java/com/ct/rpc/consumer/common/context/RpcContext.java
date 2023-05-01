package com.ct.rpc.consumer.common.context;

import com.ct.rpc.consumer.common.future.RpcFuture;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class RpcContext {

    private RpcContext(){

    }

    /**
     * RpcContext实例
     */
    private static final RpcContext AGENT = new RpcContext();

    /**
     * 存放RpcFuture的InheritableThreadLocal
     */
    private static final InheritableThreadLocal<RpcFuture> RPC_FUTURE_INHERITABLE_THREAD_LOCAL = new InheritableThreadLocal<>();

    /**
     * 获取上下文信息
     * @return
     */
    public static RpcContext getContext(){
        return AGENT;
    }

    /**
     * 将RpcFuture保存到线程的上下文
     * @param rpcFuture rpcFuture
     */
    public void setRpcFuture(RpcFuture rpcFuture){
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.set(rpcFuture);
    }

    /**
     * 获取RpcFuture
     * @return RpcFuture
     */
    public RpcFuture getRpcFuture(){
        return RPC_FUTURE_INHERITABLE_THREAD_LOCAL.get();
    }

    /**
     * 移除RpcFuture
     */
    public void removeRpcFuture(){
        RPC_FUTURE_INHERITABLE_THREAD_LOCAL.remove();
    }
}
