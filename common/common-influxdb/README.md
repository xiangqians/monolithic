# 简介

InfluxDB，时序数据库

# 安装

- 下载

https://docs.influxdata.com/influxdb/v2/install

- 启动

```shell
./influxd
```

- Web

http://localhost:8086

创建账号

```
账号：influxdbadmin
密码：influxdbadmin
```

# Spring Boot 配置

application-{profile}.yml

```yaml
influxdb:
  url: http://localhost:8086
  token: 4zKtAjyDercFgMpHBCndHzhMrBx-q3G400Mvnudm98mZ1sBuJONalemJWXywXEu-KgXmerDBWOAFp4JF3rCcSQ==
  org: test
  bucket: test
```

