package com.ct.rpc.common.helper;

/**
 * @author CT
 * @version 1.0.0
 * @description Rpc服务帮助类
 */
public class RpcServiceHelper {

    /**
     * 拼接字符串
     * @param serviceName 服务名
     * @param serviceVersion 服务版本号
     * @param group 服务分组
     * @return serviceName#serviceVersion#group
     */
    public static String buildServiceKey(String serviceName, String serviceVersion, String group){
        return String.join("#", serviceName, serviceVersion, group);
    }
}
