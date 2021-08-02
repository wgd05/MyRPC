package com.wgd.netty.codec;

import com.wgd.enums.MessageType;
import com.wgd.enums.SerializerType;
import com.wgd.format.RPCRequest;
import com.wgd.format.RPCResponse;
import com.wgd.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 自定义的编码器
 * 编码的类型是：消息类型(short) + 序列化的方式(short) + 序列化的长度(int) + 序列化的内容(byte[])
 *
 * @author wgd
 * @create 2021/08/01  23:35
 */
public class MyEncoder extends MessageToByteEncoder {

    Serializer serializer;
    SerializerType serializerType;

    //传入指定类型的序列化器
    public MyEncoder(SerializerType serializerType) {
        this.serializerType = serializerType;
        this.serializer = Serializer.getSerializer(serializerType);
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object msg, ByteBuf byteBuf) throws Exception {

        //1、写入消息类型
        if(msg instanceof RPCRequest) {
            byteBuf.writeShort(MessageType.RPCREQUEST.getCode());
        } else if(msg instanceof RPCResponse) {
            byteBuf.writeShort(MessageType.RPCRESPONSE.getCode());
        } else {
            throw new ClassNotFoundException("不支持对这种对象进行序列化");
        }
        //2、写入序列化的方式
        byteBuf.writeShort(serializerType.getCode());
        //3、进行序列化
        byte[] bytes = serializer.serialize(msg);
        //4、写入序列化后的byte数组长度
        byteBuf.writeInt(bytes.length);
        //5、写入序列化数组
        byteBuf.writeBytes(bytes);
    }
}
