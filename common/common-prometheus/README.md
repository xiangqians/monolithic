application-{profile}.yml

```yaml
# Actuator 配置
management:
  # 权限令牌
  token: kiiI6IkphFra!NUB4iLCJpYXQN^LRXN~Xo@4a1hMWF%y1hNGYODLT0skjM1u2?nKzIiWunf!Z3FpInYkYNW1Y&U
  # 开启环境变量info信息配置
  info:
    env:
      enabled: true
  endpoints:
    # org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointProperties
    web:
      exposure:
        include: '*'
    # org.springframework.boot.actuate.autoconfigure.endpoint.jmx.JmxEndpointProperties
    jmx:
      exposure:
        include: '*'
  endpoint:
    # 健康检测
    health:
      enabled: true
      show-details: always
    # 开启info端点
    info:
      enabled: true
    beans:
      enabled: true
    metrics:
      enabled: true

  metrics:
    # 设置tag
    tags:
      application: ${spring.application.name}
```
