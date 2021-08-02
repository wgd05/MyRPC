package com.wgd.enums;

/**
 * 序列化器的类型
 *
 * @author wgd
 * @create 2021/08/02  11:43
 */
public enum SerializerType {
    ORIGINSERIALIZER(0), KRYOSERIALIZER(1);

    private int code;

    SerializerType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
