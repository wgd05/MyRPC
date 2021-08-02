package com.wgd.netty.client;

import com.wgd.enums.SerializerType;
import com.wgd.format.RPCRequest;
import com.wgd.format.RPCResponse;
import com.wgd.netty.codec.MyDecoder;
import com.wgd.netty.codec.MyEncoder;
import com.wgd.netty.server.NettyRPCServerHandler;
import com.wgd.registry.ServiceDiscovery;
import com.wgd.registry.zk.impl.ServiceDiscoveryImpl;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;

import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;

/**
 * netty的客户端
 *
 * @author wgd
 * @create 2021/08/02  12:31
 */
public class NettyRPCClient {

    private String host;
    private int port;
    private ServiceDiscovery serviceDiscovery;

    private EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;

    public NettyRPCClient() {
        //注册服务发现
        serviceDiscovery = new ServiceDiscoveryImpl();

        //初始化netty客户端
        eventLoopGroup = new NioEventLoopGroup();
        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();

                        pipeline.addLast(new MyEncoder(SerializerType.KRYOSERIALIZER));
                        pipeline.addLast(new MyDecoder());
                        pipeline.addLast(new NettyRPCClientHandler());
                    }
                });
    }

    /**
     * 发送服务请求并得到结果
     * @param request
     * @return  RPCResponse 结果
     */
    public RPCResponse sendRequest(RPCRequest request) {
        //查询提供这个服务的服务器地址
        InetSocketAddress address = serviceDiscovery.serviceDiscovery(request.getInterfaceName());
        this.host = address.getHostName();
        this.port = address.getPort();

        //发送请求并得到数据
        try {
            ChannelFuture channelFuture = bootstrap.connect(host, port).sync();
            Channel channel = channelFuture.channel();
            channel.writeAndFlush(request);
            channel.closeFuture().sync();

            AttributeKey<RPCResponse> key = AttributeKey.valueOf("RPCResponse");
            RPCResponse rpcResponse = channelFuture.channel().attr(key).get();

            return rpcResponse;
        } catch (InterruptedException e) {
            System.out.println("发送请求出错！");
            e.printStackTrace();
        }

        return RPCResponse.fail();
    }
}
