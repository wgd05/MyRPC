package com.wgd.serialize;

import com.wgd.enums.MessageType;
import com.wgd.enums.SerializerType;
import com.wgd.serialize.kyro.KryoSerializer;
import com.wgd.serialize.origin.OriginSerializer;

/**
 * @author wgd
 * @create 2021/08/02  10:06
 */
public interface Serializer {

    /**
     * 序列化
     * @param obj 要序列化的对象
     * @return  返回序列化好的byte数组
     */
    byte[] serialize(Object obj);

    /**
     * 反序列化
     * @param bytes
     * @param messageType  在java中对应的类型，现在暂时为RPCRequest(0)和 RPCResponse(1)
     * @return  反序列化出来的对象
     */
    Object deserialize(byte[] bytes, MessageType messageType);

    /**
     * 使用jdk8中接口的默认实现，选择需要的序列化器
     * @param serializerType
     * @return 根据serializerType决定返回origin还是kryo
     */
    static Serializer getSerializer(SerializerType serializerType) {
        switch (serializerType) {
            case KRYOSERIALIZER:
                return new KryoSerializer();
            case ORIGINSERIALIZER:
                return new OriginSerializer();
            default:
                throw new RuntimeException("没有这种序列化器");
        }
    }
}
