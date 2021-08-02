package com.wgd.test.server.impl;

import com.wgd.test.publicinterface.HelloService;

/**
 * @author wgd
 * @create 2021/08/02  15:08
 */
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello() {
        return "测试成功啦";
    }
}
