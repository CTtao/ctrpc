package com.ct.rpc.consumer.common.future;

import com.ct.rpc.common.threadpool.ClientThreadPool;
import com.ct.rpc.consumer.common.callback.AsyncRpcCallback;
import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.request.RpcRequest;
import com.ct.rpc.protocol.response.RpcResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class RpcFuture extends CompletableFuture<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcFuture.class);

    private Sync sync;
    private RpcProtocol<RpcRequest> requestRpcProtocol;
    private RpcProtocol<RpcResponse> responseRpcProtocol;
    private long startTime;

    private long responseTimeThreshold = 5000;

    private List<AsyncRpcCallback> pendingCallbacks = new ArrayList<>();
    private ReentrantLock lock = new ReentrantLock();

    public RpcFuture(RpcProtocol<RpcRequest> requestRpcProtocol){
        this.sync = new Sync();
        this.requestRpcProtocol = requestRpcProtocol;
        this.startTime = System.currentTimeMillis();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        if (this.responseRpcProtocol != null){
            return this.responseRpcProtocol.getBody().getResult();
        } else {
            return null;
        }
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (this.responseRpcProtocol != null){
                return this.responseRpcProtocol.getBody().getResult();
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("Timeout exception. Request id: " + this.requestRpcProtocol.getHeader().getRequestId()
                    + ". Request class name: " + this.requestRpcProtocol.getBody().getClassName()
                    + ". Request method: " + this.requestRpcProtocol.getBody().getMethodName());
        }
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    public void done(RpcProtocol<RpcResponse> responseRpcProtocol){
        this.responseRpcProtocol = responseRpcProtocol;
        sync.release(1);
        //新增回调方法
        invokeCallbacks();
        //Threshold
        long responseTime = System.currentTimeMillis() - startTime;
        if (responseTime > this.responseTimeThreshold){
            LOGGER.warn("Service response time is too slow. Request id = " + responseRpcProtocol.getHeader().getRequestId() + ". Response Time = " + responseTime + "ms");
        }
    }

    /**
     * 执行回调方法
     * @param callback
     */
    private void runCallback(final AsyncRpcCallback callback){
        final RpcResponse response = this.responseRpcProtocol.getBody();
        ClientThreadPool.submit(() -> {
            if (!response.isError()){
                callback.onSuccess(response.getResult());
            } else {
                callback.onException(new RuntimeException("Response Error", new Throwable(response.getError())));
            }
        });
    }

    /**
     * 将回调接口对象存储在pendingCallbacks集合中
     * @param callback
     * @return
     */
    public RpcFuture addCallback(AsyncRpcCallback callback){
        lock.lock();
        try {
            if (isDone()){
                runCallback(callback);
            } else {
                this.pendingCallbacks.add(callback);
            }
        } finally {
            lock.unlock();
        }
        return this;
    }

    /**
     * 依次执行pendingCallbacks集合的回调方法
     */
    private void invokeCallbacks(){
        lock.lock();
        try {
            for (final AsyncRpcCallback callback : pendingCallbacks) {
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = 1L;

        //future status
        private final int done = 1;
        private final int pending = 0;

        protected boolean tryAcquire(int acquires){
            return getState() == done;
        }

        protected boolean tryRelease(int releases){
            if (getState() == pending){
                if (compareAndSetState(pending, done)){
                    return true;
                }
            }
            return false;
        }

        public boolean isDone(){
            getState();
            return getState() == done;
        }
    }
}

