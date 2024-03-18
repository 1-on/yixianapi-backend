package com.yixian.yixianapi.provider;

import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.RpcContext;

@DubboService
public class DemoServiceImpl implements DemoService {


    @Override
    public String sayHello(String name) {
        System.out.println("hello " + name + ", request from consymer: " + RpcContext.getContext().getRemoteAddress());
        return "hello " + name;
    }

    @Override
    public String sayHello2(String name) {
        return "yixian";
    }
}
