package com.wgd.registry.zk.impl;

import com.wgd.registry.ServiceRegistry;
import com.wgd.registry.zk.util.CuratorUtils;
import org.apache.curator.framework.CuratorFramework;

import java.net.InetSocketAddress;

/**
 * 基于zk的服务注册的实现类
 *
 * @author wgd
 * @create 2021/08/01  18:10
 */
public class ServiceRegistryImpl implements ServiceRegistry {
    @Override
    public void registry(String serviceName, InetSocketAddress serviceAddress) {

        CuratorFramework zkClient = CuratorUtils.getZKClient();
        CuratorUtils.createServiceNode(zkClient, serviceName, serviceAddress);
    }
}
