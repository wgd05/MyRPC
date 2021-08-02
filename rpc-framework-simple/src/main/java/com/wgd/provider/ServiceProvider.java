package com.wgd.provider;

import com.wgd.registry.ServiceRegistry;
import com.wgd.registry.zk.impl.ServiceRegistryImpl;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务提供的实现类
 *
 * @author wgd
 * @create 2021/08/01  15:59
 */
public class ServiceProvider {

    private String host;
    private int port;
    private Map<String, Object> services;
    private ServiceRegistry serviceRegistry;

    public ServiceProvider(String host, int port) {
        this.host = host;
        this.port = port;
        this.services = new ConcurrentHashMap<>();
        this.serviceRegistry = new ServiceRegistryImpl();
    }


    public void addService(Object service) {
        Class<?>[] interfaces = service.getClass().getInterfaces();
        for(Class clazz : interfaces) {
            if(services.containsKey(clazz.getName())) {
                System.out.println("该服务已经存在了，不需要重复添加！");
                continue;
            }
            //加入到本机的注册表中
            services.put(clazz.getName(), service);
            //注册到zookeeper中
            serviceRegistry.registry(clazz.getName(), new InetSocketAddress(host, port));
        }
    }


    public Object getService(String serviceName) {

        if(!services.containsKey(serviceName)) {
            System.out.println("此服务器没有这个服务");
        }
        return services.get(serviceName);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }
}
