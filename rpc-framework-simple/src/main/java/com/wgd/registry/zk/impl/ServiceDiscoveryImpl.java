package com.wgd.registry.zk.impl;

import com.wgd.registry.ServiceDiscovery;
import com.wgd.registry.zk.util.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * 基于zk的服务发现实现类
 *
 * @author wgd
 * @create 2021/08/01  18:15
 */
public class ServiceDiscoveryImpl implements ServiceDiscovery {
    @Override
    public InetSocketAddress serviceDiscovery(String serviceName) {

        CuratorFramework zkClient = CuratorUtils.getZKClient();
        List<String> services = CuratorUtils.getChildrenNodesByServiceName(zkClient, serviceName);
        String s = services.get(0);
        String[] split = s.split(":");
        InetSocketAddress inetSocketAddress = new InetSocketAddress(split[0], Integer.parseInt(split[1]));

        return inetSocketAddress;
    }
}
