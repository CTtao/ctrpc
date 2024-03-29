package com.ct.rpc.consumer.spring;

import com.ct.rpc.annotation.RpcReference;
import com.ct.rpc.constants.RpcConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author CT
 * @version 1.0.0
 * @description RpcConsumerPostProcessor
 */
@Component
public class RpcConsumerPostProcessor implements ApplicationContextAware, BeanClassLoaderAware, BeanFactoryPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(RpcConsumerPostProcessor.class);
    private ApplicationContext context;
    private ClassLoader classLoader;

    private final Map<String, BeanDefinition> rpcReferenceDefinitions = new LinkedHashMap<>();

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.context = applicationContext;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName != null){
                Class<?> clazz = ClassUtils.resolveClassName(beanClassName, this.classLoader);
                ReflectionUtils.doWithFields(clazz, this::parseRpcReference);
            }
        }

        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
        this.rpcReferenceDefinitions.forEach((beanName, beanDefinition) -> {
            if (context.containsBean(beanName)){
                throw new IllegalArgumentException("spring context already has a bean named " + beanName);
            }
            registry.registerBeanDefinition(beanName, rpcReferenceDefinitions.get(beanName));
            logger.info("registered RpcReferenceBean {} success.", beanName);
        });
    }

    private void parseRpcReference(Field field){
        RpcReference annotation = AnnotationUtils.getAnnotation(field, RpcReference.class);
        if (annotation != null){
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RpcReferenceBean.class);
            builder.setInitMethodName(RpcConstants.INIT_METHOD_NAME);
            builder.addPropertyValue("interfaceClass", field.getType());
            builder.addPropertyValue("version", annotation.version());
            builder.addPropertyValue("registryType", annotation.registryType());
            builder.addPropertyValue("registryAddress", annotation.registryAddress());
            builder.addPropertyValue("loadBalanceType", annotation.loadBalanceType());
            builder.addPropertyValue("serializationType", annotation.serializationType());
            builder.addPropertyValue("timeout", annotation.timeout());
            builder.addPropertyValue("async", annotation.async());
            builder.addPropertyValue("oneway", annotation.oneway());
            builder.addPropertyValue("proxy", annotation.proxy());
            builder.addPropertyValue("group", annotation.group());
            builder.addPropertyValue("scanNotActiveChannelInterval", annotation.scanNotActiveChannelInterval());
            builder.addPropertyValue("heartbeatInterval", annotation.heartbeatInterval());
            builder.addPropertyValue("retryInterval", annotation.retryInterval());
            builder.addPropertyValue("retryTimes", annotation.retryTimes());
            builder.addPropertyValue("enableResultCache", annotation.enableResultCache());
            builder.addPropertyValue("resultCacheExpire", annotation.resultCacheExpire());
            builder.addPropertyValue("enableDirectServer", annotation.enableDirectServer());
            builder.addPropertyValue("directServerUrl", annotation.directServerUrl());
            builder.addPropertyValue("enableDelayConnection", annotation.enableDelayConnection());
            builder.addPropertyValue("corePoolSize", annotation.corePoolSize());
            builder.addPropertyValue("maxPoolSize", annotation.maxPoolSize());
            builder.addPropertyValue("flowType", annotation.flowType());
            builder.addPropertyValue("enableBuffer", annotation.enableBuffer());
            builder.addPropertyValue("bufferSize", annotation.bufferSize());
            builder.addPropertyValue("reflectType", annotation.reflectType());
            builder.addPropertyValue("fallbackClass", annotation.fallbackClass());
            builder.addPropertyValue("fallbackClassName", annotation.fallbackClassName());
            builder.addPropertyValue("enableRateLimiter", annotation.enableRateLimiter());
            builder.addPropertyValue("rateLimiterType", annotation.rateLimiterType());
            builder.addPropertyValue("permits", annotation.permits());
            builder.addPropertyValue("milliSeconds",annotation.milliSeconds());
            builder.addPropertyValue("rateLimiterFailStrategy", annotation.rateLimiterFailStrategy());
            builder.addPropertyValue("enableFusing", annotation.enableFusing());
            builder.addPropertyValue("fusingType", annotation.fusingType());
            builder.addPropertyValue("totalFailure", annotation.totalFailure());
            builder.addPropertyValue("fusingMilliSeconds", annotation.fusingMilliSeconds());
            builder.addPropertyValue("exceptionPostProcessorType", annotation.exceptionPostProcessorType());

            BeanDefinition beanDefinition = builder.getBeanDefinition();
            rpcReferenceDefinitions.put(field.getName(), beanDefinition);
        }
    }
}
