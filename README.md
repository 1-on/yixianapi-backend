# API 接口调用平台DEMO（接入第三方API并提供在线调用地址）

（此仓库为后端代码）[前端仓库](https://github.com/1-on/yixianapi-frontend)
[toc]

> 作者：[一弦](https://github.com/1-on)

### 项目功能

- 提供 API 接口供开发者调用
- 管理员可以对接口进行增删改查、上线、下线
- 用户可以注册登录并开通接口调用权限、浏览接口、在线调试
- 开发者可以使用客户端 SDK 轻松在代码中调用接口

### 技术栈

#### 后端

- springboot 3.2.2
- MySQL 数据库
- MyBatis-Plus 以及 MyBatis X 自动生成
- API 签名认证（Http 调用）
- RPC 调用（dubbo + nacos）
- Spring Cloud Gateway 微服务网关
- Swagger + Knife4j 接口文档生成
- SpringBoot Starter (SDK 开发)

#### 前端

- React 18
- Ant Design Pro 6.x 脚手架
- Ant Design & ProComponents 组件库
- OpenAPI 前端代码生成

### 模块介绍

- yixianapi-backend 平台主要后端
- yixianapi-client-sdk 供开发者调用的SDK
- yixianapi-common rpc调用接口
- yixianapi-gateway api网关
- yixianapi-interface 模拟接口

### 项目截图

![image](https://github.com/1-on/yixianapi-backend/blob/master/doc/imgs/img.png)
![image](https://github.com/1-on/yixianapi-backend/blob/master/doc/imgs/img_1.png)
![image](https://github.com/1-on/yixianapi-backend/blob/master/doc/imgs/img_2.png)
![image](https://github.com/1-on/yixianapi-backend/blob/master/doc/imgs/img_3.png)
![image](https://github.com/1-on/yixianapi-backend/blob/master/doc/imgs/img_4.png)
![image](https://github.com/1-on/yixianapi-backend/blob/master/doc/imgs/img_5.png)
