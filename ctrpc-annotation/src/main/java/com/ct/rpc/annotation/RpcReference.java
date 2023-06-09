package com.ct.rpc.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author CT
 * @version 1.0.0
 * @description ctrpc服务消费者
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Autowired
public @interface RpcReference {

    /**
     * 版本号
     */
    String version() default "1.0.0";

    /**
     * 注册中心类型，包括：zookeeper、nacos、etcd、consul
     */
    String registryType() default "zookeeper";

    /**
     * 注册地址
     */
    String registryAddress() default "127.0.0.1:2181";

    /**
     * 负载均衡类型，默认基于zookeeper的一致性hash
     */
    String loadBalanceType() default "zkconsistenthash";

    /**
     * 序列化类型，包括：protostuff、kryo、json、jdk、hessian2、fst
     */
    String serializationType() default "protostuff";

    /**
     * 超时时间，默认5s
     */
    long timeout() default 5000;

    /**
     * 是否异步执行
     */
    boolean async() default false;

    /**
     * 是否单向调用
     */
    boolean oneway() default false;

    /**
     * 代理的类型，jdk：jdk代理，javassist：javassist代理，cglib：cglib代理
     */
    String proxy() default "jdk";

    /**
     * 服务分组，默认为空
     */
    String group() default "";

    /**
     * 心跳间隔，默认30s
     */
    int heartbeatInterval() default 30000;

    /**
     * 扫面空间连接间隔，默认60s
     */
    int scanNotActiveChannelInterval() default 60000;

    /**
     * 重试间隔时间
     */
    int retryInterval() default 1000;

    /**
     * 重试次数
     */
    int retryTimes() default 3;
}
