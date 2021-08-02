package com.wgd.netty.server;

import com.wgd.format.RPCRequest;
import com.wgd.format.RPCResponse;
import com.wgd.provider.ServiceProvider;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 自己定义的handler，处理客户端发来的请求并写出结果
 *
 * @author wgd
 * @create 2021/08/01  23:40
 */
public class NettyRPCServerHandler extends SimpleChannelInboundHandler<RPCRequest> {

    private ServiceProvider serviceProvider;

    public NettyRPCServerHandler(ServiceProvider serviceProvider) {
        this.serviceProvider = serviceProvider;
    }



    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, RPCRequest rpcRequest) throws Exception {
        System.out.println("客户端发来的请求是：" + rpcRequest);
        //处理请求
        RPCResponse response = getResponse(rpcRequest);
        //写出结果
        channelHandlerContext.writeAndFlush(response);
    }


    private RPCResponse getResponse(RPCRequest request) {
        //得到服务的接口名
        String interfaceName = request.getInterfaceName();
        //在服务器存储的Map服务中查找
        Object service = serviceProvider.getService(interfaceName);

        //根据反射获取得到结果
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParamsTypes());
            Object invoke = method.invoke(service, request.getParams());
            return RPCResponse.success(invoke);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        //如果出错则返回空
        return RPCResponse.fail();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        ctx.close();
    }
}
