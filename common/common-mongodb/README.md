# 简介

MongoDB，文档数据库

# 安装

- 下载

http://www.mongodb.org/downloads

https://www.mongodb.com/try/download/community

`7.0.11`

- 启动 MongoDB 服务

```shell
# 创建数据存储目录
mkdir data

# 创建日志目录
mkdir log

# --dbpath 参数指定 MongoDB 存储数据的路径
# --logpath 参数指定日志文件的存储路径
# --fork 参数使 MongoDB 作为守护进程在后台运行
#./bin/mongod --dbpath ./data --logpath ./log/mongodb.log --fork
./bin/mongod --dbpath ./data
```

# MongoDB Compass

MongoDB Compass 是一个官方提供的图形化管理工具，可以帮助你可视化和管理 MongoDB 数据库。

- 下载地址

https://www.mongodb.com/try/download/compass

`1.43.1 (Stable)`

- 连接到 MongoDB 数据库

`mongodb://localhost:27017`

# Spring Boot 配置

application-{profile}.yml

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://localhost:27017/yourdbname
```

