package com.wgd.serialize.origin;

import com.wgd.enums.MessageType;
import com.wgd.serialize.Serializer;

import java.io.*;

/**
 * 使用java自带的序列化器
 *
 * @author wgd
 * @create 2021/08/02  11:35
 */
public class OriginSerializer implements Serializer {
    @Override
    public byte[] serialize(Object obj) {

        byte[] bytes = null;
        try(ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            //序列化写入到bos中
            oos.writeObject(obj);
            oos.flush();
            //赋值给bytes准备返回
            bytes = bos.toByteArray();
        } catch (IOException e) {
            System.out.println("java自带序列化失败了");
            e.printStackTrace();
        }

        return bytes;
    }

    @Override
    public Object deserialize(byte[] bytes, MessageType messageType) {
        Object obj = null;

        try(ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
            ObjectInputStream ois = new ObjectInputStream(bis)) {

            obj = ois.readObject();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return obj;
    }
}
