package com.wgd.test.client;

import com.wgd.netty.client.NettyRPCClient;
import com.wgd.netty.client.NettyRPCClientProxy;
import com.wgd.test.publicinterface.HelloService;

/**
 * @author wgd
 * @create 2021/08/02  15:11
 */
public class ClientMain {

    public static void main(String[] args) {
        NettyRPCClientProxy proxy = new NettyRPCClientProxy(new NettyRPCClient());
        HelloService service = proxy.getProxy(HelloService.class);
        String hello = service.hello();
        System.out.println(hello);
    }
}
