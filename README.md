# 简介

单体式架构

- Spring Boot
- Filter（安全验证过滤器）
- MyBatis Plus
- Redis

# 项目结构

```text
├ monolithic
├── util      （工具模块）
├── biz       （业务模块）
├── schedule  （任务调度模块）
├── web       （Web模块）
└── websocket （WebSocket模块）
```

# 接口文档

[Swagger](http://localhost:8080/swagger-ui/index.html)

[Knife4j](http://localhost:8080/doc.html)

# 监控

Prometheus + Grafana

## Prometheus

### 简介

[官网](https://prometheus.io)

Prometheus 是一个开源项目，旨在深入挖掘我们的应用程序数据，通过创建过滤层来收集和分析从最简单到最复杂的所有内容。 它不仅仅关乎数字和图表，而且通过其高级查询语言和时间序列数据能力，帮助我们理解应用程序的运行状况。 集成 Prometheus 使我们能够在问题发生之前就发现问题，对系统进行微调，确保应用程序以最佳性能运行，最终为用户带来更好的体验 - 方便、快捷、可靠。

### 安装

- 下载地址

https://prometheus.io/download

`prometheus-2.53.0-rc.0.windows-amd64.zip`

- prometheus.yml

```yml
# my global config
global:
  scrape_interval: 15s # Set the scrape interval to every 15 seconds. Default is every 1 minute.
  evaluation_interval: 15s # Evaluate rules every 15 seconds. The default is every 1 minute.
  # scrape_timeout is set to the global default (10s).

# Alertmanager configuration
alerting:
  alertmanagers:
    - static_configs:
        - targets:
          # - alertmanager:9093

# Load rules once and periodically evaluate them according to the global 'evaluation_interval'.
rule_files:
# - "first_rules.yml"
# - "second_rules.yml"

# A scrape configuration containing exactly one endpoint to scrape:
# Here it's Prometheus itself.
scrape_configs:
  # The job name is added as a label `job=<job_name>` to any timeseries scraped from this config.
  - job_name: "prometheus"

    # metrics_path defaults to '/metrics'
    # scheme defaults to 'http'.

    static_configs:
      - targets: [ "localhost:9090" ]
```

- Web

[首页](http://localhost:9090)

[查看监控对象列表](http://localhost:9090/targets)

### exporter

#### windows_exporter

监控 Windows 系统

- 安装 windows_exporter

https://github.com/prometheus-community

https://github.com/prometheus-community/windows_exporter/releases/tag/v0.26.0-rc.2

`windows_exporter-0.26.0-amd64.msi`

- Prometheus 配置

prometheus.yml

```yml
scrape_configs:
  - job_name: 'Windows'
    static_configs:
      # windows_exporter 默认运行端口是 9182
      - targets: [ 'localhost:9182' ]
```

#### mysqld_exporter

监控 MySQL 关系型数据库

- 安装 mysqld_exporter

https://prometheus.io/download

`mysqld_exporter-0.15.1.windows-amd64.zip`

- mysqld_exporter 配置

添加 `.my.cnf` 文件

```cnf
[client]
host=localhost
port=3306
user=root
password=root
```

- Prometheus 配置

prometheus.yml

```yml
scrape_configs:
  - job_name: 'MySQL'
    static_configs:
      # mysqld_exporter 默认运行端口是 9104
      - targets: [ 'localhost:9104' ]
```

#### redis_exporter

监控 Redis 非关系型数据库





#### SpringBoot 集成 Prometheus

监控 SpringBoot 应用

- SpringBoot 配置

https://springdoc.cn/spring-boot-prometheus

首先，将 Spring Boot Actuator 和 Micrometer Prometheus Registry 添加到项目的依赖中。 Actuator 提供了一系列内置端点，用于显示运行应用的性能信息，如健康状况、指标等。 Micrometer Prometheus registry 会将这些指标格式化为 Prometheus 可读格式。


application.yml

```yml
management:
  endpoints:
    web:
      exposure:
        include: '*'
  endpoint:
    health:
      show-details: always
```

这个配置确保 Actuator 暴露 `/actuator/prometheus` 端点，该端点以 Prometheus 可以抓取的格式提供了丰富的应用指标。 需要注意的是，公开所有的 Actuator 端点（`management.endpoints.web.exposure.include=*`）在开发过程中可以提供有用的洞察力，但可能会暴露敏感的运行数据。对于生产环境，最佳实践是根据监控和运营需求慎重地选择要公开的端点。

通过上述这些步骤，我们的 Spring Boot 应用现在将公开有价值的指标，供 Prometheus 收集和存储。这个基础设置对于我们监控策略的下一阶段非常关键，包括使用 Prometheus 抓取这些指标，并使用 Grafana 等工具进行可视化展示。

- Prometheus 配置

prometheus.yml

```yml
scrape_configs:
  - job_name: 'web'
    metrics_path: '/actuator/prometheus'
    scrape_interval: 15s
    static_configs:
      - targets: [ 'localhost:8080' ]
```

## Grafana

### 简介

[官网](https://grafana.com)

### 安装

- 下载地址

https://grafana.com/grafana/download

`grafana-enterprise-11.0.0.windows-amd64.zip`

`bin/grafana-server.exe`

- defaults.ini

功能模块分布

| 功能 | 配置文件中模块 |
| --- | --- |
| 登录用户名/密码 | \[security\] |
| 用户管理 | \[users\] |
| 匿名登录 | \[auth.anonymous\] |
| 邮件配置 | \[smtp\] |

```ini

```

- Web

[首页](http://localhost:3000)

用户名：admin
密码：admin

### 添加 Prometheus 数据源

- 添加数据源

![Add new connection](doc/image/add_new_connection.png)

![Add new data source](doc/image/add_new_data_source.png)

![Data source](doc/image/data_source.png)

- 添加 Windows 仪表板

https://grafana.com/grafana/dashboards

搜索：windows

仪表板ID：10467

![New dashboard](doc/image/new_dashboard.png)

![Load dashboard](doc/image/load_dashboard.png)

![Import dashboard](doc/image/import_dashboard.png)


- 添加 MySQL 仪表板

https://grafana.com/grafana/dashboards

搜索：mysql

`MySQL Overview`

仪表板ID：7362


- 添加 Redis 仪表板


- 添加 SpringBoot 仪表板







