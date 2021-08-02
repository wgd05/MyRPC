package com.wgd.format;

import com.wgd.serialize.Serializer;

import java.io.Serializable;
import java.util.Arrays;

/**
 * 请求数据的格式
 *
 * @author wgd
 * @create 2021/08/02  9:42
 */
public class RPCRequest implements Serializable {

    private String interfaceName;
    private String methodName;
    private Object[] params;
    private Class<?>[] paramsTypes;

    public RPCRequest(String interfaceName, String methodName, Object[] params, Class<?>[] paramsTypes) {
        this.interfaceName = interfaceName;
        this.methodName = methodName;
        this.params = params;
        this.paramsTypes = paramsTypes;
    }
    public RPCRequest(){}

    public String getInterfaceName() {
        return interfaceName;
    }

    public void setInterfaceName(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public Class<?>[] getParamsTypes() {
        return paramsTypes;
    }

    public void setParamsTypes(Class<?>[] paramsTypes) {
        this.paramsTypes = paramsTypes;
    }

    @Override
    public String toString() {
        return "RPCRequest{" +
                "interfaceName='" + interfaceName + '\'' +
                ", methodName='" + methodName + '\'' +
                ", params=" + Arrays.toString(params) +
                ", paramsTypes=" + Arrays.toString(paramsTypes) +
                '}';
    }
}
