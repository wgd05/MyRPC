package com.wgd.netty.server;


import com.wgd.enums.SerializerType;
import com.wgd.netty.codec.MyDecoder;
import com.wgd.netty.codec.MyEncoder;
import com.wgd.provider.ServiceProvider;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

import java.util.PriorityQueue;


/**
 * @author wgd
 * @create 2021/08/01  15:28
 */
public class NettyRPCServer {

    private String host;
    private int port;
    private ServiceProvider serviceProvider;

    public NettyRPCServer(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }

    public void start() {
        start0();
    }

    private void start0() {
        host = serviceProvider.getHost();
        port = serviceProvider.getPort();

        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline pipeline = socketChannel.pipeline();

                            pipeline.addLast(new MyEncoder(SerializerType.KRYOSERIALIZER));
                            pipeline.addLast(new MyDecoder());
                            pipeline.addLast(new NettyRPCServerHandler(serviceProvider));
                        }
                    });

            ChannelFuture channelFuture = serverBootstrap.bind(host, port).sync();
            System.out.println(host + ":" + port + " " + "服务器启动了");
            channelFuture.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            System.out.println("服务器启动失败了！");
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}
