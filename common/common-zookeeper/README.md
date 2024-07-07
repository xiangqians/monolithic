# Spring Boot 配置

application-{profile}.yml

```yaml
# zookeeper 配置
zookeeper:
  # 为 CuratorFramework 实例设置命名空间，所有操作都会在该命名空间下进行
  namespace: my-namespace
  # 服务器连接地址，集群模式则使用逗号分隔如：host1:port1,host2:port2
  connect-string: localhost:2180
  # 会话超时时间，时间格式：{n}h{n}m{n}s
  session-timeout: 10s
  # 连接超时时间，时间格式：{n}h{n}m{n}s
  connection-timeout: 10s
  # 重试策略
  retry-policy:
    # 初始化间隔时间
    base-sleep-time: 1s
    # 最大重试次数
    max-retries: 3
    # 最大重试间隔时间
    max-sleep: 30s
```
