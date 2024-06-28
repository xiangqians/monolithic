# 简介

# MySQL 配置

确保 MySQL 数据库已经配置启用 binlog

- my.ini

```ini
[mysqld]
# MySQL 服务器的唯一标识符，每个服务器应具有唯一的 ID
server-id=1

# 启用二进制日志功能，并指定二进制日志文件的前缀名称
# 若不指定绝对路径则默认当前目录下Data文件夹下 mysql-bin.xxxxxx
log-bin=mysql-bin

# 指定 binlog 的格式，可以选择 ROW、STATEMENT 或 MIXED
# ROW 格式，在修改表结构时，产生大量的二进制日志？
binlog_format=row

# 设置二进制日志文件过期时间，单位：天
# 超过指定天数的日志文件会被自动删除
#expire_logs_days=7

# 设置每个二进制日志文件最大大小，单位：K、M、G
# 当二进制日志文件达到该大小时，MySQL 会自动创建新的日志文件
#max_binlog_size=100M

# 二进制日志文件缓存大小
#binlog_cache_size=4M

# 最大二进制日志文件缓存大小
#max_binlog_cache_size=512M
```

- 查看 MySQL 是否已经开启 binlog

```text
mysql> SHOW VARIABLES LIKE 'server_id';
+---------------+-------+
| Variable_name | Value |
+---------------+-------+
| server_id     | 1     |
+---------------+-------+
1 row in set (0.07 sec)


mysql> SHOW VARIABLES LIKE 'log_bin';
+---------------+-------+
| Variable_name | Value |
+---------------+-------+
| log_bin       | ON    |
+---------------+-------+
1 row in set (0.01 sec)


mysql> SHOW VARIABLES LIKE 'binlog_format';
+---------------+-------+
| Variable_name | Value |
+---------------+-------+
| binlog_format | ROW   |
+---------------+-------+
1 row in set (0.07 sec)


mysql> SHOW VARIABLES LIKE 'expire_logs_days';
+------------------+-------+
| Variable_name    | Value |
+------------------+-------+
| expire_logs_days | 0     |
+------------------+-------+
1 row in set (0.07 sec)


mysql> SHOW VARIABLES LIKE 'max_binlog_size';
+-----------------+------------+
| Variable_name   | Value      |
+-----------------+------------+
| max_binlog_size | 1073741824 |
+-----------------+------------+
1 row in set (0.07 sec)
```

- 查看 MySQL binlog 模式

```text
mysql> SHOW GLOBAL VARIABLES LIKE "binlog%";
+--------------------------------------------+--------------+
| Variable_name                              | Value        |
+--------------------------------------------+--------------+
| binlog_cache_size                          | 32768        |
| binlog_checksum                            | CRC32        |
| binlog_direct_non_transactional_updates    | OFF          |
| binlog_error_action                        | ABORT_SERVER |
| binlog_expire_logs_seconds                 | 2592000      |
| binlog_format                              | ROW          |
| binlog_group_commit_sync_delay             | 0            |
| binlog_group_commit_sync_no_delay_count    | 0            |
| binlog_gtid_simple_recovery                | ON           |
| binlog_max_flush_queue_time                | 0            |
| binlog_order_commits                       | ON           |
| binlog_row_image                           | FULL         |
| binlog_row_metadata                        | MINIMAL      |
| binlog_row_value_options                   |              |
| binlog_rows_query_log_events               | OFF          |
| binlog_stmt_cache_size                     | 32768        |
| binlog_transaction_dependency_history_size | 25000        |
| binlog_transaction_dependency_tracking     | COMMIT_ORDER |
+--------------------------------------------+--------------+
18 rows in set (0.52 sec)
```

- 查看所有二进制日志文件列表

```text
mysql> SHOW MASTER LOGS;
```

- 查看最新一个二进制日志文件的编号名称，及其最后一个操作事件结束点

```text
mysql> SHOW MASTER STATUS;
```

- 刷新日志，立刻产生一个新编号的二进制日志文件，跟重启一个效果

```text
mysql> FLUSH LOGS;
```

- 清空所有二进制日志文件

```text
mysql> RESET MASTER;
```

- 查看二进制日志文件事件

```text
mysql> SHOW BINLOG EVENTS IN 'mysql-bin.000001' LIMIT 10;
```
