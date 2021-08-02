package com.wgd.format;

import java.io.Serializable;

/**
 * 返回数据的格式
 *
 *
 * @author wgd
 * @create 2021/08/02  9:43
 */
public class RPCResponse implements Serializable {

    private String code;
    private String message;
    private Object data;
    private Class<?> dataType;

    private RPCResponse(String code, String message, Object data, Class<?> dataType) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.dataType = dataType;
    }
    public RPCResponse(){}

    public static RPCResponse success(Object data) {
        return new RPCResponse("200", null, data, data.getClass());
    }
    public static RPCResponse fail() {
        return new RPCResponse("500", "服务器出错了", null, null);
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }

    public Class<?> getDataType() {
        return dataType;
    }

    @Override
    public String toString() {
        return "RPCResponse{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                ", dataType=" + dataType +
                '}';
    }
}
