package com.ct.rpc.annotation;

import com.ct.rpc.constants.RpcConstants;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author CT
 * @version 1.0.0
 * @description ctrpc服务提供者注解
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface RpcService {

    /**
     * 接口的class
     */
    Class<?> interfaceClass() default void.class;

    /**
     * 接口的className
     */
    String interfaceClassName() default "";

    /**
     * 版本号
     */
    String version() default RpcConstants.RPC_COMMON_DEFAULT_VERSION;

    /**
     * 服务分组，默认为空
     */
    String group() default RpcConstants.RPC_COMMON_DEFAULT_GROUP;

    /**
     * 权重
     */
    int weight() default 0;
}
