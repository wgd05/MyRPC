package com.wgd.registry;

import java.net.InetSocketAddress;

/**
 * 服务发现接口
 *
 * @author wgd
 * @create 2021/08/01  18:12
 */
public interface ServiceDiscovery {

    /**
     * 服务发现
     * @param serviceName  服务名字
     * @return  拥有此服务的服务器地址
     */
    InetSocketAddress serviceDiscovery(String serviceName);
}
