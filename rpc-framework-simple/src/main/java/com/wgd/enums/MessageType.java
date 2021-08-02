package com.wgd.enums;

/**
 * 消息类型
 *
 * @author wgd
 * @create 2021/08/02  10:51
 */
public enum MessageType {

    RPCREQUEST(0), RPCRESPONSE(1);

    private int code;

    MessageType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
