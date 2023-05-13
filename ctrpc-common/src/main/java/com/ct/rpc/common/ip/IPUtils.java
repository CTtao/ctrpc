package com.ct.rpc.common.ip;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;

/**
 * @author CT
 * @version 1.0.0
 * @description IP工具类
 */
public class IPUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(IPUtils.class);

    public static InetAddress getLocalInetAddress(){
        try {
            return InetAddress.getLocalHost();
        } catch (Exception e){
            LOGGER.error("get local ip address throws exception: {}", e);
        }
        return null;
    }

    public static String getLocalAddress(){
        return getLocalInetAddress().toString();
    }

    public static String getLocalHostName(){
        return getLocalInetAddress().getHostName();
    }

    public static String getLocalHostIP(){
        return getLocalInetAddress().getHostAddress();
    }
}
