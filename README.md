# 简介

一体化架构

springboot, cache, security, oauth2

# Document

[Knife4j](http://localhost:8080/doc.html)
[Swagger](http://localhost:8080/swagger-ui.html)

# tmp

- 抛弃 po vo dto概念，直接使用entity。 （如此一来，文档就不规范了。vo，参数和响应实体都以Vo作为后缀命名，如：XxxVo）
- 每个功能模块新建一个包名，如：sys、log、order等等

响应式编程？


Add, Del, Upd, Get

# 四种授权方式

## 授权码方式

1. 拼接url，访问授权接口


1、授权码方式
1）输入地址，将会重定向到登陆页面，填写用户账号密码
http://127.0.0.1:8088/oauth2/authorize?client_id=oidc-client&response_type=code&scope=read&redirect_uri=http://127.0.0.1:8088/authorized

2）
http://127.0.0.1:8088/oauth2/authorize?client_id=oidc-client&response_type=code&scope=read&redirect_uri=

1、获取token（密码模式）
GET http://localhost:8080/oauth/token?client_id=iot&client_secret=123456&grant_type=password&scope=all&username=admin&password=123456

2、校验token
GET http://localhost:9000/oauth/check_token?token=4de1bfdc-b960-46ff-8e51-272e95a051c1

3、刷新token
GET http://localhost:9000/oauth/token?client_id=iot&client_secret=123456&grant_type=refresh_token&refresh_token=bc1c7c28-9dea-4b37-a5ef-755fe41c6db1

