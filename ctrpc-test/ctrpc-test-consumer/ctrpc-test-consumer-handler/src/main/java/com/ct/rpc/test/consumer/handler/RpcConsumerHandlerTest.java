package com.ct.rpc.test.consumer.handler;

import com.ct.rpc.consumer.common.RpcConsumer;
import com.ct.rpc.consumer.common.future.RpcFuture;
import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.header.RpcHeaderFactory;
import com.ct.rpc.protocol.request.RpcRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CT
 * @version 1.0.0
 * @description
 */
public class RpcConsumerHandlerTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcConsumerHandlerTest.class);

    public static void main(String[] args) throws Exception{
        RpcConsumer consumer = RpcConsumer.getInstance();
        RpcFuture future = consumer.sendRequest(getRpcRequestProtocol());
        LOGGER.info("从服务消费者获取到的数据===>>>" + future.get());
        consumer.close();
    }


    private static RpcProtocol<RpcRequest> getRpcRequestProtocol(){
        //模拟一份发送数据
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk"));
        RpcRequest request = new RpcRequest();
        request.setClassName("com.ct.rpc.test.api.DemoService");
        request.setGroup("ct");
        request.setMethodName("hello");
        request.setParameters(new Object[]{"ct"});
        request.setParameterTypes(new Class[]{String.class});
        request.setVersion("1.0.0");
        request.setAsync(false);
        request.setOneway(false);
        protocol.setBody(request);
        return protocol;
    }
}
