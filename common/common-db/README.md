application.yml

```yaml
# MyBatis配置
mybatis-plus:
  configuration:
    # 开启驼峰配置
    map-underscore-to-camel-case: true

    # MyBatis自动映射策略：MyBatis在将数据库查询结果映射到Java对象时的自动映射策略
    # 在MyBatis中，有以下几种自动映射策略：
    # 1、NONE：不启用自动映射
    # 2、PARTIAL：只对非嵌套的 resultMap 进行自动映射
    # 3、FULL：对所有的 resultMap 都进行自动映射
    auto-mapping-behavior: full

    # 开启打印sql配置
    # 1、org.apache.ibatis.logging.nologging.NoLoggingImpl
    # 2、org.apache.ibatis.logging.stdout.StdOutImpl
    # 3、org.apache.ibatis.logging.slf4j.Slf4jImpl
    log-impl: org.apache.ibatis.logging.slf4j.Slf4jImpl

  # 指定sql映射文件位置
  mapper-locations: classpath:mybatis/mapper/**/*Mapper.xml

  # 全局配置
  global-config:
    # 逻辑删除配置
    db-config:
      # 删除前
      logic-not-delete-value: 0
      # 删除后
      logic-delete-value: 1
```

application-{profile}.yml

```yaml
spring:
  # 数据源配置
  datasource:
    # JDBC驱动类名
    driver-class-name: com.mysql.cj.jdbc.Driver
    # 数据库连接URL，指定了数据库类型、地址、端口和数据库名称
    url: jdbc:mysql://localhost:3306/monolithic?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true
    # 数据库用户名
    username: root
    # 数据库密码
    password: root
    # 指定使用的数据源
    type: com.zaxxer.hikari.HikariDataSource
    # hikari连接池
    hikari:
      # 连接池中最大连接数（包括空闲和正在使用的连接）
      maximum-pool-size: 5
      # 连接池中最小空闲连接数，即最少保持多少个空闲连接
      minimum-idle: 1
```
