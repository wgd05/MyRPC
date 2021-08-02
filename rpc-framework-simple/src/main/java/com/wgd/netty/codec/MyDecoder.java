package com.wgd.netty.codec;

import com.wgd.enums.MessageType;
import com.wgd.enums.SerializerType;
import com.wgd.serialize.Serializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 自定义解码器
 * 编码的类型是：消息类型(short) + 序列化的方式(short) + 序列化的长度(int) + 序列化的内容(byte[])
 *
 * @author wgd
 * @create 2021/08/01  23:35
 */
public class MyDecoder extends ByteToMessageDecoder {

    //需要反序列化的类型
    MessageType messageType;
    //对应的序列化器类型
    SerializerType serializerType;

    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list) throws Exception {

        //1、根据消息类型获得对应的类型
        short messageTypeCode = byteBuf.readShort();
        if(messageTypeCode == MessageType.RPCREQUEST.getCode()) {
            messageType = MessageType.RPCREQUEST;
        } else if(messageTypeCode == MessageType.RPCRESPONSE.getCode()) {
            messageType = MessageType.RPCRESPONSE;
        } else {
            throw new ClassNotFoundException("不支持对这个对象进行解码");
        }

        //2、读取使用的序列化器
        short serializerCode = byteBuf.readShort();
        if(serializerCode == SerializerType.KRYOSERIALIZER.getCode()) {
            serializerType = SerializerType.KRYOSERIALIZER;
        } else if(serializerCode == SerializerType.ORIGINSERIALIZER.getCode()) {
            serializerType = SerializerType.ORIGINSERIALIZER;
        } else {
            throw new ClassNotFoundException("没有这种序列化器");
        }

        //3、得到对应的序列化器
        Serializer serializer = Serializer.getSerializer(serializerType);
        //4、读取数据的长度
        int length = byteBuf.readInt();
        //5、读取byte数组写入到bytes中
        byte[] bytes = new byte[length];
        byteBuf.readBytes(bytes);
        //6、byte数组反序列化为指定的对象
        Object deserialize = serializer.deserialize(bytes, messageType);
        list.add(deserialize);
    }
}
