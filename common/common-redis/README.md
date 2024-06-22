application-{profile}.yml

```yaml
spring:
  data:
    # Redis配置
    redis:
      # 主机
      host: localhost
      # 端口
      port: 6379
      # 密码
      #password:
      # 数据库
      database: 0
      # 连接超时
      connect-timeout: 60s
      # 读超时
      timeout: 60s
      # Lettuce客户端配置
      lettuce:
        # 连接池配置
        pool:
          # 允许最大连接数，默认8（负值表示没有限制），推荐值：大于cpu * 2，通常为 (cpu * 2) + 2
          max-active: 8
          # 最大空闲连接数，默认8，推荐值：cpu * 2
          max-idle: 8
          # 最小空闲连接数，默认0
          min-idle: 0
          # 从连接池获取连接，最多等待时间，超过该时间抛出异常，默认-1（表示没有限制）
          max-wait: 5s

  # Redis Redisson配置
  redis:
    redisson:
      file: classpath:redisson-{profile}.yml
```

redisson-{profile}.yml

```yaml
# https://github.com/redisson/redisson/wiki/Table-of-Content
# https://springdoc.cn/spring-boot-redisson

# 线程池数量，默认值：当前处理核数量 * 2
#threads: 8
# Netty线程池数量，默认值：当前处理核数量 * 2
#nettyThreads: 8
# 编码
#codec: !<org.redisson.codec.Kryo5Codec> { }
#codec: !<org.redisson.codec.JsonJacksonCodec> { }
# 传输模式，默认NIO，可选参数：
# TransportMode.NIO,
# TransportMode.EPOLL - 需要依赖里有netty-transport-native-epoll包（Linux）
# TransportMode.KQUEUE - 需要依赖里有 netty-transport-native-kqueue包（macOS）
#transportMode: "NIO"

# Redis单节点配置 {@link org.redisson.config.SingleServerConfig}
# 单节点配置可以兼容redis的配置方式
singleServerConfig:
  # 服务器地址，格式为：redis://host:port
  address: "redis://localhost:6379"
  # 用户名
  username: null
  # 密码，如果没有密码则为 null
  password: null
  # 数据库
  database: 0
  # 连接超时时间，单位：毫秒
  connectTimeout: 10000
  # 命令等待超时时间，单位：毫秒
  timeout: 3000
  # 连接池大小
  connectionPoolSize: 8
  # 最小空闲连接数
  connectionMinimumIdleSize: 8
  # 连接空闲超时时间，单位：毫秒
  idleConnectionTimeout: 10000
  # 命令重试次数
  # 命令失败重试次数,如果尝试达到 retryAttempts（命令失败重试次数） 仍然不能将命令发送至某个指定的节点时，将抛出错误。
  # 如果尝试在此限制之内发送成功，则开始启用 timeout（命令等待超时） 计时。
  retryAttempts: 3
  # 命令重试间隔时间，单位：毫秒
  retryInterval: 1500
  # 单个连接最大订阅数量
  subscriptionsPerConnection: 5
  # 客户端名称
  clientName: null
  # 发布和订阅连接的最小空闲连接数
  subscriptionConnectionMinimumIdleSize: 1
  # 发布和订阅连接池大小
  subscriptionConnectionPoolSize: 8
  # DNS监测时间间隔，单位：毫秒
  dnsMonitoringInterval: 5000

# Redis集群配置 {@link org.redisson.config.ClusterServersConfig}
#clusterServersConfig:
#  # 集群节点地址
#  nodeAddresses:
#    - "redis://localhost:6379"
#    - "redis://localhost:6380"
#  # 密码
#  password: null
#  # Redis集群不支持多个数据库的概念，默认只有一个数据库，即 db 0，所以这里是没有 database 这个参数
#  #database: 0
#  # 如果当前连接池里的连接数量超过了最小空闲连接数，而同时有连接空闲时间超过了该数值，
#  # 那么这些连接将会自动被关闭，并从连接池里去掉。时间单位是毫秒。默认10000
#  idleConnectionTimeout: 1000
#  # 同节点建立连接时的等待超时。时间单位是毫秒。默认10000
#  connectTimeout: 1000
#  # 等待节点回复命令的时间。该时间从命令发送成功时开始计时。默认3000
#  timeout: 1000
#  # 命令失败重试次数,如果尝试达到 retryAttempts（命令失败重试次数） 仍然不能将命令发送至某个指定的节点时，将抛出错误。
#  # 如果尝试在此限制之内发送成功，则开始启用 timeout（命令等待超时） 计时。
#  retryAttempts: 3
#  # 在一条命令发送失败以后，等待重试发送的时间间隔。时间单位是毫秒。默认1500
#  retryInterval: 1500
#  # 失败从节点重连间隔时间
#  failedSlaveReconnectionInterval: 3000
#  # 失败从节点校验间隔时间
#  failedSlaveCheckInterval: 60000
#  # 每个连接的最大订阅数量。默认5
#  subscriptionsPerConnection: 5
#  # 在Redis节点里显示的客户端名称。默认null
#  clientName: lizz
#  # 在使用多个Redis服务节点的环境里，可以选用以下几种负载均衡方式选择一个节点：
#  # org.redisson.connection.balancer.WeightedRoundRobinBalancer - 权重轮询调度算法
#  # org.redisson.connection.balancer.RoundRobinLoadBalancer - 轮询调度算法
#  # org.redisson.connection.balancer.RandomLoadBalancer - 随机调度算法
#  # 默认：RoundRobinLoadBalancer
#  loadBalancer: !<org.redisson.connection.balancer.RoundRobinLoadBalancer> { }
#  # 用于发布和订阅连接的最小保持连接数（长连接）。Redisson内部经常通过发布和订阅来实现许多功能。
#  # 长期保持一定数量的发布订阅连接是必须的。默认1
#  subscriptionConnectionMinimumIdleSize: 1
#  # 多从节点的环境里，每个从服务节点里用于发布和订阅连接的连接池最大容量。连接池的连接数量自动弹性伸缩。默认50
#  subscriptionConnectionPoolSize: 50
#  # 多从节点的环境里，每个从服务节点里用于普通操作（非 发布和订阅）的最小保持连接数（长连接）。
#  # 长期保持一定数量的连接有利于提高瞬时读取反映速度。默认32
#  slaveConnectionMinimumIdleSize: 32
#  # 多从节点的环境里，每个从服务节点里用于普通操作（非 发布和订阅）连接的连接池最大容量。连接池的连接数量自动弹性伸缩。默认64
#  slaveConnectionPoolSize: 64
#  # 多从节点的环境里，每个主节点的最小保持连接数（长连接）。长期保持一定数量的连接有利于提高瞬时写入反应速度。默认32
#  masterConnectionMinimumIdleSize: 32
#  # 多主节点的环境里，每个主节点的连接池最大容量。连接池的连接数量自动弹性伸缩。 默认64
#  masterConnectionPoolSize: 64
#  # 设置读取操作选择节点的模式。 可用值为：
#  # SLAVE - 只在从服务节点里读取。 默认
#  # MASTER - 只在主服务节点里读取。
#  # MASTER_SLAVE - 在主从服务节点里都可以读取。
#  readMode: "SLAVE"
#  # 设置订阅操作选择节点的模式。可用值为：
#  # SLAVE - 只在从服务节点里订阅。默认
#  # MASTER - 只在主服务节点里订阅。
#  subscriptionMode: "SLAVE"
#  # 对主节点变化节点状态扫描的时间间隔。单位是毫秒。
#  scanInterval: 1000
#  # ping连接间隔
#  pingConnectionInterval: 0
#  # 是否保持连接
#  keepAlive: false
#  #
#  tcpNoDelay: false
```

