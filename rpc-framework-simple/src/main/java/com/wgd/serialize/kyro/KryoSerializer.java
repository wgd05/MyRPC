package com.wgd.serialize.kyro;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.wgd.enums.MessageType;
import com.wgd.format.RPCRequest;
import com.wgd.format.RPCResponse;
import com.wgd.serialize.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.function.Supplier;

/**
 * 使用kryo序列化与反序列化
 *
 * @author wgd
 * @create 2021/08/02  11:00
 */
public class KryoSerializer implements Serializer {

    //因为kryo不是线程安全的，所以使用ThreadLocal来存储kryo对象
    private final ThreadLocal<Kryo> kryoThreadLocal = ThreadLocal.withInitial(new Supplier<Kryo>() {
        @Override
        public Kryo get() {
            Kryo kryo = new Kryo();
            kryo.register(RPCRequest.class);
            kryo.register(RPCResponse.class);
            return kryo;
        }
    });

    @Override
    public byte[] serialize(Object obj) {

        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Output output = new Output(bos)) {

            Kryo kryo = kryoThreadLocal.get();
            //将对象序列化为byte数组，存在output中
            kryo.writeObject(output, obj);
            kryoThreadLocal.remove();
            return output.toBytes();
        } catch (Exception e) {
            System.out.println("Kryo序列化失败！");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object deserialize(byte[] bytes, MessageType messageType) {

        try(ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            Input input = new Input(bis);) {

            Kryo kryo = kryoThreadLocal.get();
            //要转化成的对象
            Object obj = null;
            switch (messageType) {
                case RPCREQUEST:
                    obj = kryo.readObject(input, RPCRequest.class);
                    break;
                case RPCRESPONSE:
                    obj = kryo.readObject(input, RPCResponse.class);
                    break;
                default:
                    break;
            }
            kryoThreadLocal.remove();

            return obj;
        } catch (Exception e) {
            System.out.println("Kryo反序列化失败！");
            e.printStackTrace();
        }
        return null;
    }
}
