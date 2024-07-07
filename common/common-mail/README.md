# Spring Boot 配置

application-{profile}.yml

```yaml
spring:
  # 邮件配置
  # https://springdoc.cn/spring-boot/application-properties.html#appendix.application-properties.mail
  mail:
    # 邮件SMTP服务器地址，如：
    # 1、smtp.qq.com
    #   需要在QQ邮箱设置中开启 SMTP 服务，并且记得把配置文件中的 spring.mail.password 替换为你的 授权码。注意，不是 QQ 密码，关于 QQ 邮箱如何生成授权码你可以参阅 官方文档（https://service.mail.qq.com/detail/0/75）
    # 2、smtp.163.com
    #   【设置】->【POP3/SMTP/IMAP】->开启【POP3/SMTP服务】
    host: ${MONOLITHIC_MONITOR_MAIL_HOST:smtp.example.com}
    # 端口
    port: ${MONOLITHIC_MONITOR_MAIL_PORT:25}
    # 使用的协议
    protocol: smtp
    # 默认编码
    default-encoding: UTF-8
    # 用户名
    username: ${MONOLITHIC_MONITOR_MAIL_USERNAME:your-email@example.com}
    # 密码或授权码
    password: ${MONOLITHIC_MONITOR_MAIL_PASSWORD:your-email-password}
    # 发件人邮箱地址
    from: ${spring.mail.username}
    # 其他的属性
    properties:
      "mail.smtp.connectiontimeout": 5000
      "mail.smtp.timeout": 3000
      "mail.smtp.writetimeout": 5000
      # 设置是否需要认证，如果为true,那么用户名和密码就必须的
      # 如果设置false，可以不设置用户名和密码，当然也得看你的对接的平台是否支持无密码进行访问的
      "mail.smtp.auth": true
      # STARTTLS 是对纯文本通信协议的扩展。它提供一种方式将纯文本连接升级为加密连接（TLS或SSL），而不是另外使用一个端口作加密通信
      "mail.smtp.starttls.enable": true
      "mail.smtp.starttls.required": true
      "mail.smtp.ssl.enable": true
      "mail.debug": true
```
