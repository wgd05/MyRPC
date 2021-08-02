package com.wgd.registry;

import java.net.InetSocketAddress;

/**
 * 服务注册接口
 *
 * @author wgd
 * @create 2021/08/01  16:01
 */
public interface ServiceRegistry {

    /**
     * 注册服务
     * @param serviceName
     * @param serviceAddress
     */
    void registry(String serviceName, InetSocketAddress serviceAddress);
}
