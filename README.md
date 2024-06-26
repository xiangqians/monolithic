# 简介

单体式架构

- Spring Boot
- Filter（安全验证过滤器）
- MyBatis Plus
- MySQL
- ClickHouse
- Redis
- MongoDB
- Prometheus
- InfluxDB
- MinIO
- Kafka
- Neo4j

# 项目结构

```text
├ monolithic
├── common                  （公共模块）
├    ├── common-clickhouse  （列式数据库模块）
├    ├── common-influxdb    （时序数据库模块）
├    ├── common-mail        （邮件模块）
├    ├── common-minio       （对象存储模块）
├    ├── common-mongodb     （文档数据库模块）
├    ├── common-mysql       （行式数据库模块）
├    ├── common-neo4j       （图数据库模块）
├    ├── common-producer    （生产者模块）
├    ├── common-prometheus  （监控模块）
├    ├── common-redis       （缓存模块）
├    ├── common-thread-pool （线程池模块）
├    └── common-util        （工具模块）
├── consumer                （消费者模块）    【监听端口：8300】
├── scheduler               （任务调度模块）  【监听端口：8200】
├── web                     （Web模块）      【监听端口：8000】
└── websocket               （WebSocket模块）【监听端口：8100】
```
