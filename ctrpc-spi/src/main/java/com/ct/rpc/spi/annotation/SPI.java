package com.ct.rpc.spi.annotation;

import java.lang.annotation.*;

/**
 * @author CT
 * @version 1.0.0
 * @description @SPI
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {

    /**
     * 默认实现方式
     */
    String value() default "";
}
