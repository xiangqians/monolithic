# 简介

MinIO 是一个高性能的对象存储服务器，兼容 Amazon S3 API。

# 安装

## Linux 下安装

- 下载

https://min.io

- startup.sh

```shell
#!/bin/bash

# /path/to/data 是 MinIO 用来存储数据的目录路径
#minio server /path/to/data
minio server /path/to/data >/dev/null 2>&1 &
```

- MinIO Console

http://localhost:9000

```
默认账号：minioadmin
默认密码：minioadmin
```

- MinIO API

http://localhost:9000

## Windows 下安装

- 下载

https://min.io

- startup.bat

```batch
:: 设置命令提示符窗口的标题为 "MinIO Server"
@title MinIO Server

:: 启用命令回显，使命令行窗口显示执行的每个命令
@echo on

minio.exe server ./data

:: 暂停脚本执行，并等待用户按下任意键继续
pause
```

# Spring Boot 配置

application-{profile}.yml

```yaml
minio:
  endpoint: http://localhost:9002
  access-key: m0dWHllxoipmIczh
  secret-key: Kr2Uq0tCHr0HjLLBHBiYzDs5tznle4jv
```
