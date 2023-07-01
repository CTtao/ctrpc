package com.ct.rpc.test.consumer.handler;

import com.ct.rpc.common.exception.RegistryException;
import com.ct.rpc.consumer.common.RpcConsumer;
import com.ct.rpc.consumer.common.context.RpcContext;
import com.ct.rpc.protocol.enumeration.RpcType;
import com.ct.rpc.proxy.api.callback.AsyncRpcCallback;
import com.ct.rpc.proxy.api.future.RpcFuture;
import com.ct.rpc.protocol.RpcProtocol;
import com.ct.rpc.protocol.header.RpcHeaderFactory;
import com.ct.rpc.protocol.request.RpcRequest;
import com.ct.rpc.registry.api.RegistryService;
import com.ct.rpc.registry.api.config.RegistryConfig;
import com.ct.rpc.registry.zookeeper.ZookeeperRegistryService;
import com.ct.rpc.spi.loader.ExtensionLoader;
import org.apache.commons.lang3.StringUtils;
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
        RpcConsumer consumer = RpcConsumer.getInstance(30000, 60000, 1000, 3);
        //1.同步调用
        RpcFuture future = consumer.sendRequest(getRpcRequestProtocol(), getRegistryService("127.0.0.1:2181","zookeeper","random"));
        future.addCallback(new AsyncRpcCallback() {
            @Override
            public void onSuccess(Object result) {
                LOGGER.info("从服务消费者获取到的数据===>>>" + result);
            }

            @Override
            public void onException(Exception e) {
                LOGGER.info("抛出了异常===>>>" + e);
            }
        });
        Thread.sleep(200);
        //2.异步调用
//        consumer.sendRequest(getRpcRequestProtocol());
////        RpcFuture future = RpcContext.getContext().getRpcFuture();
////        LOGGER.info("从服务消费者获取到的数据===>>>" + future.get());
        //3.单向
//        LOGGER.info("无需获取返回数据");
        consumer.close();
    }

    public static void mainAsync(String[] args) throws Exception{
        RpcConsumer consumer = RpcConsumer.getInstance(30000,60000,1000, 3);
        consumer.sendRequest(getRpcRequestProtocol(), getRegistryService("127.0.0.1:2181","zookeeper","random"));
        RpcFuture future = RpcContext.getContext().getRpcFuture();
        LOGGER.info("从服务消费者获取到的数据===>>>" + future.get());
        consumer.close();
    }

    private static RegistryService getRegistryService(String registryAddress, String registryType, String registryLoadBalanceType) {
        if (StringUtils.isEmpty(registryType)){
            throw new IllegalArgumentException("registry type is null");
        }
        //TODO 后续SPI扩展
        RegistryService registryService = ExtensionLoader.getExtension(RegistryService.class, registryType);
        try {
            registryService.init(new RegistryConfig(registryAddress, registryType, registryLoadBalanceType));
        } catch (Exception e) {
            LOGGER.error("RpcClient init registry service throws exception:{}", e);
            throw new RegistryException(e.getMessage(), e);
        }
        return registryService;
    }

    public static void mainSync(String[] args) throws Exception{
        RpcConsumer consumer = RpcConsumer.getInstance(30000, 60000, 1000, 3);
        RpcFuture future = consumer.sendRequest(getRpcRequestProtocol(), getRegistryService("127.0.0.1:2181","zookeeper","random"));
        LOGGER.info("从服务消费者获取到的数据===>>>" + future.get());
        consumer.close();
    }


    private static RpcProtocol<RpcRequest> getRpcRequestProtocol(){
        //模拟一份发送数据
        RpcProtocol<RpcRequest> protocol = new RpcProtocol<>();
        protocol.setHeader(RpcHeaderFactory.getRequestHeader("jdk", RpcType.REQUEST.getType()));
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
