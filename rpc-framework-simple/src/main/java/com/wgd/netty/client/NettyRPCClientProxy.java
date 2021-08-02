package com.wgd.netty.client;

import com.wgd.format.RPCRequest;
import com.wgd.format.RPCResponse;
import com.wgd.registry.ServiceDiscovery;
import com.wgd.registry.zk.impl.ServiceDiscoveryImpl;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 客户端的动态代理类，屏蔽了发送请求和收到数据的过程
 *
 * @author wgd
 * @create 2021/08/02  14:37
 */
public class NettyRPCClientProxy implements InvocationHandler {

    NettyRPCClient nettyRPCClient;

    public NettyRPCClientProxy(NettyRPCClient nettyRPCClient) {
        this.nettyRPCClient = nettyRPCClient;
    }

    /**
     * 增强方法，增加发送请求和返回数据
     * @param proxy
     * @param method
     * @param args
     * @return 得到的data
     * @throws Throwable
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //构造request
        RPCRequest request = new RPCRequest(method.getDeclaringClass().getName(), method.getName(), args, method.getParameterTypes());
        //得到response
        RPCResponse response = nettyRPCClient.sendRequest(request);
        //返回response中的data
        return response.getData();
    }

    /**
     * 得到一个动态代理结果，屏蔽了发送请求和得到数据的过程
     * @param clazz
     * @param <T>
     * @return 返回该类型的动态代理
     */
    public <T> T getProxy(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, this);
    }
}
