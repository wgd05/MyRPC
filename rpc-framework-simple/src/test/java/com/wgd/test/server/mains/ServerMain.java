package com.wgd.test.server.mains;

import com.wgd.netty.server.NettyRPCServer;
import com.wgd.provider.ServiceProvider;
import com.wgd.test.server.impl.HelloServiceImpl;

/**
 * @author wgd
 * @create 2021/08/02  15:09
 */
public class ServerMain {

    public static void main(String[] args) {
        ServiceProvider serviceProvider = new ServiceProvider("127.0.0.1", 8899);
        serviceProvider.addService(new HelloServiceImpl());
        NettyRPCServer nettyRPCServer = new NettyRPCServer(serviceProvider);
        nettyRPCServer.start();
    }
}
