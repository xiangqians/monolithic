# 简介

MySQL 是行式存储数据库

- 数据按行存储，适合事务处理（OLTP）。
- 优化了写操作，适合频繁的数据插入和更新。

# MySQL 配置

MySQL 的默认配置文件是 `/etc/mysql/my.cnf` 文件。

如果想要自定义配置，建议向 `/etc/mysql/conf.d` 目录中创建 `.cnf` 文件。 新建的文件可以任意起名，只要保证后缀名是 `cnf` 即可。新建的文件中的配置项可以覆盖 `/etc/mysql/my.cnf` 中的配置项。

```shell
# 查看全部配置信息
show variables;

# 查看以character_set开头的信息
show variable like 'character_set%';

# mysql查看服务器当前运行状态的信息
# 查看mysql服务器当前运行状态的信息，包括连接数，查询执行次数，缓存命中率等。显示的是运行时的统计数据，反映了服务器当前的活动和性能。用于监视服务器的运行情况和性能分析.
show status;
show status like 'Threads%';
```

- `/etc/mysql/my.cnf`

```conf
# Copyright (c) 2017, Oracle and/or its affiliates. All rights reserved.
#
# This program is free software; you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation; version 2 of the License.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program; if not, write to the Free Software
# Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA

#
# The MySQL  Server configuration file.
#
# For explanations see
# http://dev.mysql.com/doc/mysql/en/server-system-variables.html

[mysqld]
pid-file        = /var/run/mysqld/mysqld.pid
socket          = /var/run/mysqld/mysqld.sock
datadir         = /var/lib/mysql
secure-file-priv= NULL

# Custom config should go here
!includedir /etc/mysql/conf.d/
```

- `/etc/mysql/conf.d/client.cnf`

```conf
# 客户端设置，当前为客户端默认参数
[client]

# 默认连接端口为 3306
port=3306

# 本地连接的 socket 套接字
socket=/tmp/mysql.sock

# 设置字符集，通常使用 uft8 即可
default_character_set=utf8
```

- `/etc/mysql/conf.d/safe.cnf`

```conf
# mysqld_safe 是服务器端工具，用于启动 mysqld，也是 mysqld 的守护进程。
# 当 mysql 被 kill 时，mysqld_safe 负责重启启动它。
[mysqld_safe]

# 此为 MySQL 打开的文件描述符限制，它是 MySQL 中的一个全局变量且不可动态修改。它控制着 mysqld 进程能使用的最大文件描述符数量。默认最小值为 1024
# 需要注意的是这个变量的值并不一定是你在这里设置的值，mysqld 会在系统允许的情况下尽量取最大值。
# 当 open_files_limit 没有被配置时，比较 max_connections*5 和 ulimit -n 的值，取最大值
# 当 open_file_limit 被配置时，比较 open_files_limit 和 max_connections*5 的值，取最大值
open_files_limit=8192

# 用户名
user=mysql

# 错误 log 记录文件
log-error=error.log
```

- `/etc/mysql/conf.d/mysqld.cnf`

```conf
[mysqld]
# pid文件位置
pid-file=/var/run/mysqld/mysqld.pid
# MySQL 客户端程序和服务器之间的本地通讯指定一个套接字文件
socket=/var/run/mysqld/mysqld.sock
# MySQL 数据文件存放目录
datadir=/var/lib/mysql
# 指定服务器可以从哪些目录读取LOAD DATA INFILE请求的文件
secure-file-priv=NULL

# MySQL服务实例的唯一编号，每个MySQL服务实例Id需唯一，在主从复制中使用
#server-id=1

# 指定MySQL服务器绑定的IP地址，默认为0.0.0.0（所有可用IP）
# 通过bind-address参数可以控制mysql监听哪些ip地址。如果不设置该参数，则默认会监听所有可用ip地址
# 可以利用bind-address提高mysql的安全性，比如我们可以将mysql绑定到局域网内部的ip地址而不是公网ip地址，以此防止被外界攻击
# 127.0.0.1，限制 MySQL 只监听本地地址，避免外部访问
#bind-address=127.0.0.1
# 禁用网络连接，只允许本地连接，增加安全性
skip-networking

# 允许最大接收数据包的大小，防止服务器发送过大的数据包。
# 当发出长查询或 mysqld 返回较大结果时，mysqld 才会分配内存，所以增大这个值风险不大，默认 16M，也可以根据需求改大，但太大会有溢出风险。取较小值是一种安全措施，避免偶然出现但大数据包导致内存溢出。
max_allowed_packet=16M
# 设置客户端发送给 MySQL 服务器的最大数据包大小为 128MB
max_allowed_packet=128M
# 接受的数据包大小；增加该变量的值十分安全，这是因为仅当需要时才会分配额外内存。例如，仅当你发出长查询或MySQLd必须返回大的结果行时MySQLd才会分配更多内存。
# 该变量之所以取较小默认值是一种预防措施，以捕获客户端和服务器之间的错误信息包，并确保不会因偶然使用大的信息包而导致内存溢出。
max_allowed_packet = 4M

# 创建数据表时，默认使用的存储引擎。这个变量还可以通过 –default-table-type 进行设置
default_storage_engine = InnoDB 

# 最大连接数，当前服务器允许多少并发连接。默认为 100，一般设置为小于 1000 即可。太高会导致内存占用过多，MySQL 服务器会卡死。作为参考，小型站设置 100 - 300
max_connections  = 512

# 用户最大的连接数，默认值为 50 一般使用默认即可。
max_user_connections = 50

# 线程缓存，用于缓存空闲的线程。这个数表示可重新使用保存在缓存中的线程数，当对方断开连接时，如果缓存还有空间，那么客户端的线程就会被放到缓存中，以便提高系统性能。我们可根据物理内存来对这个值进行设置，对应规则 1G 为 8；2G 为 16；3G 为 32；4G 为 64 等。
thread_cache_size = 64


# 设置为 0 时，则禁用查询缓存（尽管仍分配query_cache_size个字节的缓冲区）。
# 设置为 1 时 ，除非指定SQL_NO_CACHE，否则所有SELECT查询都将被缓存。
# 设置为 2 时，则仅缓存带有SQL CACHE子句的查询。
# 请注意，如果在禁用查询缓存的情况下启动服务器，则无法在运行时启用服务器。
query_cache_type = 1 


# 缓存select语句和结果集大小的参数。
# 查询缓存会存储一个select查询的文本与被传送到客户端的相应结果。
# 如果之后接收到一个相同的查询，服务器会从查询缓存中检索结果，而不是再次分析和执行这个同样的查询。
# 如果你的环境中写操作很少，读操作频繁，那么打开query_cache_type=1，会对性能有明显提升。如果写操作频繁，则应该关闭它（query_cache_type=0）。
query_cache_size = 64M


# MySQL 执行排序时，使用的缓存大小。增大这个缓存，提高 group by，order by 的执行速度。
Session variables
sort_buffer_size = 2M

# HEAP 临时数据表的最大长度，超过这个长度的临时数据表 MySQL 可根据需求自动将基于内存的 HEAP 临时表改为基于硬盘的 MyISAM 表。我们可通过调整 tmp_table_size 的参数达到提高连接查询速度的效果。
tmp_table_size = 32M

# MySQL 读入缓存的大小。如果对表对顺序请求比较频繁对话，可通过增加该变量值以提高性能。
read_buffer_size  = 128k

# 用于表的随机读取，读取时每个线程分配的缓存区大小。默认为 256k ，一般在 128 - 256k之间。在做 order by 排序操作时，会用到 read_rnd_buffer_size 空间来暂做缓冲空间。
read_rnd_buffer_size = 256k


# 程序中经常会出现一些两表或多表 Join （联表查询）的操作。为了减少参与 Join 连表的读取次数以提高性能，需要用到 Join Buffer 来协助 Join 完成操作。当 Join Buffer 太小时，MySQL 不会将它写入磁盘文件。和 sort_buffer_size 一样，此参数的内存分配也是每个连接独享。
join_buffer_size  = 128k 

# 限制不使用文件描述符存储在缓存中的表定义的数量。
table_definition_cache = 400

# 限制为所有线程在内存中打开的表数量。
table_open_cache   = 400                                


MySQL 错误日志设置
# log_warnings 为0， 表示不记录告警信息。
# log_warnings 为1， 表示告警信息写入错误日志。
# log_warnings 大于1， 表示各类告警信息，例如有关网络故障的信息和重新连接信息写入错误日志。
log_warnings = 2
log_error = error.log


# MySQL 慢查询记录
# slow_query_log ：全局开启慢查询功能。
# slow_query_log_file ：指定慢查询日志存储文件的地址和文件名。
# log_queries_not_using_indexes：无论是否超时，未被索引的记录也会记录下来。
# long_query_time：慢查询阈值（秒），SQL 执行超过这个阈值将被记录在日志中。
# min_examined_row_limit：慢查询仅记录扫描行数大于此参数的 SQL。
slow_query_log_file = slow.log
slow_query_log  = 0
log_queries_not_using_indexes  = 1
long_query_time = 0.5
min_examined_row_limit = 100


# MySQL 全局查询日志
# 这一段比较好理解，存放文件名，是否开启日志记录

general_log_file = general.log
general_log = 0


https://developer.aliyun.com/article/822935




# 指定MySQL服务器监听的端口号，默认3306
#port=3306

# 【字符集】
# 指定服务器上创建新数据库时默认使用的字符集
character-set-server=utf8mb4
# 数据库字符集对应一些排序规则，要属于character-set-server对应值的集合内
collation-server=utf8mb4_general_ci
# 设置client连接MySQL时的字符集，防止乱码
init_connect='SET NAMES utf8mb4'

# 创建新表时将使用的默认存储引擎
default-storge-engine=INNODB


# 密码认证方式
# 设置密码验证规则
# mysql8 默认的是caching_sha2_password， 老版的默认是 mysql_native_password
#default_authentication_plugin=mysql_native_password

# 设置时区为东八区
default-time_zone='+8:00'

# 设置sql模式 sql_mode模式引起的分组查询出现 this is incompatible with sql_mode=only_full_group_by，这里最好剔除ONLY_FULL_GROUP_BY
sql_mode=STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION
# 设置MySQL的SQL模式
# NO_ENGINE_SUBSTITUTION 表示如果指定的存储引擎不存在，将会产生错误
# STRICT_TRANS_TABLES 启用严格的事务模式，确保数据的一致性和完整性
sql_mode=NO_ENGINE_SUBSTITUTION,STRICT_TRANS_TABLES

# MySQL支持4种事务隔离级别，他们分别是：
# READ-UNCOMMITTED（读取未提交内容）
# READ-COMMITTED （读取提交内容）
# REPEATABLE-READ （可重复读）
# SERIALIZABLE （可串行化）
# 如没有指定，MySQL默认采用的是REPEATABLE-READ，ORACLE默认的是READ-COMMITTED
transaction_isolation = REPEATABLE-READ


# 允许最大连接数 默认：151
/*
设置mysql实例最大允许的并发连接数。决定了mysql服务器能够同事处理的客户端连接数的上限
max_connections不能设置的过高，否则会占用过多的系统资源，导致性能下降或服务器宕机等
*/
max_connections=500




# 对于同一主机，如果有超出该参数值个数的中断错误连接，则该主机将被禁止连接。如需对该主机进行解禁，执行：FLUSH HOST。默认：100
# 可以有效防止dos攻击
max_connect_errors = 6000

# MySQL能有的连接数量。当主要MySQL线程在一个很短时间内得到非常多的连接请求，这就起作用，默认：151
# 然后主线程花些时间(尽管很短)检查连接并且启动一个新线程。back_log值指出在MySQL暂时停止回答新请求之前的短时间内多少个请求可以被存在堆栈中。
# 如果期望在一个短时间内有很多连接，你需要增加它。也就是说，如果MySQL的连接数据达到max_connections时，新来的请求将会被存在堆栈中，以等待某一连接释放资源，该堆栈的数量即back_log，如果等待连接的数量超过back_log，将不被授予连接资源。
# 另外，这值（back_log）限于您的操作系统对到来的TCP/IP连接的侦听队列的大小。
# 你的操作系统在这个队列大小上有它自己的限制（可以检查你的OS文档找出这个变量的最大值），试图设定back_log高于你的操作系统的限制将是无效的。
back_log = 600

# MySQL打开的文件描述符限制，当open_file_limit被配置的时候，比较open_files_limit和max_connections*5的值，哪个大用哪个。MySQL8.0 默认：65536
open_files_limit = 65536


# MySQL每打开一个表，都会读入一些数据到table_open_cache缓存中，当MySQL在这个缓存中找不到相应信息时，才会去磁盘上读取，MySQL8.0 默认：4000
# 假定系统有200个并发连接，则需将此参数设置为200*N(N为每个连接所需的文件描述符数目)；
# 当把table_open_cache设置为很大时，如果系统处理不了那么多文件描述符，那么就会出现客户端失效，连接不上
table_open_cache = 4000



# 一个事务，在没有提交的时候，产生的日志，记录到Cache中；等到事务提交需要提交的时候，则把日志持久化到磁盘。默认binlog_cache_size大小32K
binlog_cache_size = 1M

# 定义了用户可以创建的内存表(memory table)的大小。这个值用来计算内存表的最大行数值。这个变量支持动态改变
max_heap_table_size = 8M

# MySQL的heap（堆积）表缓冲大小。所有联合在一个DML指令内完成，并且大多数联合甚至可以不用临时表即可以完成。
# 大多数临时表是基于内存的(HEAP)表。具有大的记录长度的临时表 (所有列的长度的和)或包含BLOB列的表存储在硬盘上。
# 如果某个内部heap（堆积）表大小超过tmp_table_size，MySQL可以根据需要自动将内存中的heap表改为基于硬盘的MyISAM表。还可以通过设置tmp_table_size选项来增加临时表的大小。也就是说，如果调高该值，MySQL同时将增加heap表的大小，可达到提高联接查询速度的效果
tmp_table_size = 16M

# MySQL读入缓冲区大小。对表进行顺序扫描的请求将分配一个读入缓冲区，MySQL会为它分配一段内存缓冲区。read_buffer_size变量控制这一缓冲区的大小。
# 如果对表的顺序扫描请求非常频繁，并且你认为频繁扫描进行得太慢，可以通过增加该变量值以及内存缓冲区大小提高其性能
read_buffer_size = 2M
# MySQL的随机读缓冲区大小。当按任意顺序读取行时(例如，按照排序顺序)，将分配一个随机读缓存区。进行排序查询时，
# MySQL会首先扫描一遍该缓冲，以避免磁盘搜索，提高查询速度，如果需要排序大量数据，可适当调高该值。但MySQL会为每个客户连接发放该缓冲空间，所以应尽量适当设置该值，以避免内存开销过大
read_rnd_buffer_size = 8M
# MySQL执行排序使用的缓冲大小。如果想要增加ORDER BY的速度，首先看是否可以让MySQL使用索引而不是额外的排序阶段。
# 如果不能，可以尝试增加sort_buffer_size变量的大小
sort_buffer_size = 8M

# 联合查询操作所能使用的缓冲区大小，和sort_buffer_size一样，该参数对应的分配内存也是每连接独享
join_buffer_size = 8M

# 这个值（默认8）表示可以重新利用保存在缓存中线程的数量，当断开连接时如果缓存中还有空间，那么客户端的线程将被放到缓存中，
# 如果线程重新被请求，那么请求将从缓存中读取,如果缓存中是空的或者是新的请求，那么这个线程将被重新创建,如果有很多新的线程，
# 增加这个值可以改善系统性能.通过比较Connections和Threads_created状态的变量，可以看到这个变量的作用。(–>表示要调整的值)
# 根据物理内存设置规则如下：
# 1G  —> 8
# 2G  —> 16
# 3G  —> 32
# 大于3G  —> 64
thread_cache_size = 8

#指定用于索引的缓冲区大小，增加它可得到更好处理的索引(对所有读和多重写)，到你能负担得起那样多。如果你使它太大，
# 系统将开始换页并且真的变慢了。对于内存在4GB左右的服务器该参数可设置为384M或512M。通过检查状态值Key_read_requests和Key_reads，
# 可以知道key_buffer_size设置是否合理。比例key_reads/key_read_requests应该尽可能的低，
# 至少是1:100，1:1000更好(上述状态值可以使用SHOW STATUS LIKE 'key_read%'获得)。注意：该参数值设置的过大反而会是服务器整体效率降低
key_buffer_size = 4M

# 分词词汇最小长度，默认4
ft_min_word_len = 4

# 限制Innodb能打开的表的数据，如果库里的表特别多的情况，请增加这个
innodb_open_files = 4000

# InnoDB使用一个缓冲池来保存索引和原始数据,这里你设置越大,你在存取表里面数据时所需要的磁盘I/O越少.
# 在一个独立使用的数据库服务器上,你可以设置这个变量到服务器物理内存大小的75%，不要设置过大, 否则, 由于物理内存的竞争可能导致操作系统的换页颠簸.
# 注意在32位系统上你每个进程可能被限制在 2-3.5G 用户层面内存限制,所以不要设置的太高. 默认值128M
# 可以通过下列语句来判断本机所有类型数据库总共需要的空间（数据库索引+数据总大小）：
/*
SELECT ENGINE,
ROUND(SUM(data_length + index_length) /1024/1024, 1) AS "Total MB"
FROM INFORMATION_SCHEMA.TABLES WHERE table_schema not in
('information_schema', 'performance_schema')
GROUP BY ENGINE;
*/
innodb_buffer_pool_size = 128M



# innodb使用后台线程处理数据页上的读写 I/O(输入输出)请求,根据你的 CPU 核数来更改,默认是4
# 注:这两个参数不支持动态改变,需要把该参数加入到my.cnf里，修改完后重启MySQL服务,允许值的范围从 1-64
innodb_write_io_threads = 4
innodb_read_io_threads = 4
# 默认设置为 0,表示不限制并发数，这里推荐设置为0，更好去发挥CPU多核处理能力，提高并发量
innodb_thread_concurrency = 0
# InnoDB中的清除操作是一类定期回收无用数据的操作。在之前的几个版本中，清除操作是主线程的一部分，这意味着运行时它可能会堵塞其它的数据库操作。
# 从MySQL5.5.X版本开始，该操作运行于独立的线程中,并支持更多的并发数。用户可通过设置innodb_purge_threads配置参数来选择清除操作是否使用单独线程,默认情况下参数设置为0(不使用单独线程),设置为 1 时表示使用单独的清除线程。建议为1
innodb_purge_threads = 1

/*

控制MySQL磁盘写入策略的，即如何将数据库在缓存中的数据向磁盘中同步，我们有三个可选参数0、1、2，在解释这三个参数的不同机制之前，我们要先搞清楚对数据库的操作是如何同步到磁盘中的，过程如下：
对数据库进行插入了操作--》操作存入log_buffer--》log_buffer将数据同步到log file中--》log file将数据同步到物理磁盘中。

它的三个参数及机制如下：

0：log buffer将每秒一次地写入log file中，并且log file的flush(刷到磁盘)操作同时进行。该模式下在事务提交的时候，不会主动触发写入磁盘的操作。
1：每次事务提交时MySQL都会把log buffer的数据写入log file，并且flush(刷到磁盘中去)，该模式为系统默认。
2：每次事务提交时MySQL都会把log buffer的数据写入log file，但是flush(刷到磁盘中去)操作并不会同时进行。该模式下，MySQL会每秒执行一次 flush(刷到磁盘)操作。

如果我们对数据安全性要求比较高度，则使用默认值1。
*/
# innodb_flush_log_at_trx_commit=0,表示每隔一秒把log buffer刷到文件系统中(os buffer)去，并且调用文件系统的“flush”操作将缓存刷新到磁盘上去。也就是说一秒之前的日志都保存在日志缓冲区，也就是内存上，如果机器宕掉，可能丢失1秒的事务数据。
# 主线程中每秒会将重做日志缓冲写入磁盘的重做日志文件(REDO LOG)中。不论事务是否已经提交）默认的日志文件是ib_logfile0,ib_logfile1
# innodb_flush_log_at_trx_commit=1，表示在每次事务提交的时候，都把log buffer刷到文件系统中(os buffer)去，并且调用文件系统的“flush”操作将缓存刷新到磁盘上去。这样的话，数据库对IO的要求就非常高了，如果底层的硬件提供的IOPS比较差，那么MySQL数据库的并发很快就会由于硬件IO的问题而无法提升。
# innodb_flush_log_at_trx_commit=2，表示在每次事务提交的时候会把log buffer刷到文件系统中去，但并不会立即刷写到磁盘。如果只是MySQL数据库挂掉了，由于文件系统没有问题，那么对应的事务数据并没有丢失。只有在数据库所在的主机操作系统损坏或者突然掉电的情况下，数据库的事务数据可能丢失1秒之类的事务数据。这样的好处，减少了事务数据丢失的概率，而对底层硬件的IO要求也没有那么高(log buffer写到文件系统中，一般只是从log buffer的内存转移的文件系统的内存缓存中，对底层IO没有压力)
# 每次事务提交的时候将数据写入事务日志，而这里的写入仅是调用了文件系统的写入操作，而文件系统是有 缓存的，所以这个写入并不能保证数据已经写入到物理磁盘
# 默认值1是为了保证完整的ACID。当然，你可以将这个配置项设为1以外的值来换取更高的性能，但是在系统崩溃的时候，你将会丢失1秒的数据。
# 设为0的话，mysqld进程崩溃的时候，就会丢失最后1秒的事务。设为2,只有在操作系统崩溃或者断电的时候才会丢失最后1秒的数据。InnoDB在做恢复的时候会忽略这个值。
# 总结
# 设为1当然是最安全的，但性能页是最差的（相对其他两个参数而言，但不是不能接受）。如果对数据一致性和完整性要求不高，完全可以设为2，如果只最求性能，例如高并发写的日志服务器，设为0来获得更高性能
# 默认：1
innodb_flush_log_at_trx_commit = 2

# 此参数确定些日志文件所用的内存大小，以M为单位。缓冲区更大能提高性能，但意外的故障将会丢失数据。MySQL8.0默认为16M
innodb_log_buffer_size = 16M

# 此参数确定数据日志文件的大小，更大的设置可以提高性能，但也会增加恢复故障数据库所需的时间，MySQL8.0默认为48M
innodb_log_file_size = 48M
# 为提高性能，MySQL可以以循环方式将日志文件写到多个文件。MySQL8.0默认为2
innodb_log_files_in_group = 2
# innodb主线程刷新缓存池中的数据，使脏数据比例小于90%
innodb_max_dirty_pages_pct = 90
# InnoDB事务在被回滚之前可以等待一个锁定的超时秒数。InnoDB在它自己的锁定表中自动检测事务死锁并且回滚事务。InnoDB用LOCK TABLES语句注意到锁定设置。默认值是50秒
innodb_lock_wait_timeout = 60 

# 批量插入缓存大小， 这个参数是针对MyISAM存储引擎来说的。适用于在一次性插入100-1000+条记录时， 提高效率。默认值是8M。可以针对数据量的大小，翻倍增加。
bulk_insert_buffer_size = 8M
# MyISAM设置恢复表之时使用的缓冲区的尺寸，当在REPAIR TABLE或用CREATE INDEX创建索引或ALTER TABLE过程中排序 MyISAM索引分配的缓冲区
myisam_sort_buffer_size = 8M
# 如果临时文件会变得超过索引，不要使用快速排序索引方法来创建一个索引。注释：这个参数以字节的形式给出
myisam_max_sort_file_size = 10G
# 如果该值大于1，在Repair by sorting过程中并行创建MyISAM表索引(每个索引在自己的线程内) 
myisam_repair_threads = 1


# 服务器关闭交互式连接前等待活动的秒数。交互式客户端定义为在mysql_real_connect()中使用CLIENT_INTERACTIVE选项的客户端。默认值：28800秒（8小时）
interactive_timeout = 28800
# 服务器关闭非交互连接之前等待活动的秒数。在线程启动时，根据全局wait_timeout值或全局interactive_timeout值初始化会话wait_timeout值，
# 取决于客户端类型(由mysql_real_connect()的连接选项CLIENT_INTERACTIVE定义)。参数默认值：28800秒（8小时）
# MySQL服务器所支持的最大连接数是有上限的，因为每个连接的建立都会消耗内存，因此我们希望客户端在连接到MySQL Server处理完相应的操作后，应该断开连接并释放占用的内存。如果你的MySQL Server有大量的闲置连接，他们不仅会白白消耗内存，而且如果连接一直在累加而不断开，最终肯定会达到MySQL Server的连接上限数，这会报'too many connections'的错误。对于wait_timeout的值设定，应该根据系统的运行情况来判断。
# 在系统运行一段时间后，可以通过show processlist命令查看当前系统的连接状态，如果发现有大量的sleep状态的连接进程，则说明该参数设置的过大，可以进行适当的调整小些。要同时设置interactive_timeout和wait_timeout才会生效。
wait_timeout = 28800

# 错误日志位置
log_error = /var/log/mysqld.log
# 日志自动过期清理天数
expire_logs_days = 90

# 是否开启慢查询 日志收集
show_query_log=1
# 慢查询日志位置
slow_query_log_file = /data/local/mysql-5.7.19/log/mysql-slow.log
# 设置记录慢查询超时时间
long_query_time = 1

# binlog 配置，
log_bin = /var/lib/mysql/binlog
# binlog最大值
max_binlog_size = 1024M
# 规定binlog的格式，binlog有三种格式statement、row、mixad，MySQL8.0之前默认使用statement，MySQL8.0默认使用row格式，建议使用row格式
# ROW：日志中会记录成每一行数据被修改的形式，然后在 slave 端再对相同的数据进行修改。
# Statement：每一条会修改数据的 SQL 都会记录到 master 的 bin-log 中。slave 在复制的时候 SQL 进程会解析成和原来 master 端执行过的相同的 SQL 再次执行。
# Mixed：在 Mixed 模式下，MySQL 会根据执行的每一条具体的 SQL 语句来区分对待记录的日志形式，也就是在 statement 和 row 之间选择一种。
binlog-format = ROW
# 在提交n次事务后，进行binlog的落盘，0为不进行强行的刷新操作，而是由文件系统控制刷新日志文件，如果是在线交易和账有关的数据建议设置成1，如果是其他数据可以保持为0即可
sync_binlog = 1




```



https://www.cnblogs.com/architectforest/p/12491984.html


```conf


# 指定在内存中存储的临时表的最大大小限制为 128MB
max_heap_table_size=128M

# 指定允许的临时表的最大大小为 16MB
tmp_table_size=16M

# 缓存打开的表对象数量，以提高查询效率
table_open_cache=8000

# 设置随机读取缓冲区的大小为 16MB，用于执行全表扫描和排序操作
read_rnd_buffer_size=16M

# 强制表名在存储和查询时不区分大小写，这里设置为 1 表示启用。
#lower_case_table_names=1

# 设置 MySQL 进程能够打开的最大文件数
open_files_limit = 100000



# 设置二进制日志文件自动清理的天数为 3 天
expire_logs_days=3



# 在从服务器上记录对于主服务器二进制日志事件的更新
log-slave-updates=on

# 在从服务器遇到错误时跳过所有错误
slave-skip-errors=all

# 指定每写入多少次日志就强制将日志写入磁盘
sync_binlog=100


# 设置 InnoDB 缓冲池的大小为 100GB，用于缓存表数据和索引
innodb_buffer_pool_size=100G

# 将 InnoDB 缓冲池分成 50 个实例，以提高并发访问的效率
innodb_buffer_pool_instances=50

# 指定每个 InnoDB 缓冲池实例的大小为 1GB
innodb_buffer_pool_chunk_size=1G

# 设置排序操作的缓冲区大小为 16MB
sort_buffer_size=16M

# 设置允许排序数据的最大长度为 8096 字节
max_length_for_sort_data=8096

# 设置 InnoDB 排序缓冲区的大小为 64MB
innodb_sort_buffer_size=64M

# 设置 MyISAM 排序缓冲区的大小为 64MB
myisam_sort_buffer_size=64M

# 设置连接操作的缓冲区大小为 8MB
join_buffer_size=8M

# 设置读取操作的缓冲区大小为 8MB
read_buffer_size=8M

# 设置 MySQL 服务器等待连接断开的超时时间为 36000 秒
wait_timeout=36000

# 设置交互式连接的超时时间为 36000 秒
interactive_timeout=36000

# 禁用 MySQL X 协议，不监听 33060 端口
mysqlx=0

# 设置 MySQL X 协议的交互式连接超时时间为 36000 秒
mysqlx_interactive_timeout=36000

# 设置 MySQL X 协议的等待超时时间为 36000 秒
mysqlx_wait_timeout=36000



[mysql]
# 设置 MySQL 客户端的默认字符集为 UTF-8
default-character-set=utf8mb4

# 指定 MySQL 客户端连接时使用的 socket 文件路径
socket=/data/database/mysql.sock


[client]
# 指定 MySQL 客户端连接时使用的 socket 文件路径
socket=/data/database/mysql.sock:

# 指定 MySQL 客户端连接的端口号
port=3306

# 设置 MySQL 客户端的默认字符集为 UTF-8
default-character-set=UTF8MB4
```


- `/etc/mysql/conf.d/security.cnf`（安全配置）

```conf
[mysqld]

```

- `/etc/mysql/conf.d/log.cnf`（日志配置）

```conf
[mysqld]
# 错误和警告日志文件
log_error=/var/log/mysql/error.log

【通用查询（所有SQL查询）日志】（一般生产环境不推荐开启）
# 开启通用查询日志记录
general_log=1
# 通用查询日志文件
general_log_file=/var/log/mysql/general.log

# 【二进制日志】
# 启用二进制日志，并指定二进制日志文件的位置
log-bin=/var/log/mysql/mysql-bin
# 设置MySQL服务器在生成二进制日志时忽略 mysql 数据库的更新操作
binlog-ignore-db=mysql
# MySQL二进制日志文件保存的过期时间，过期后自动删除
# 默认值是0，表示不限制，这样会占用空间太多
expire_logs_days=365
# 限制单个文件大小，默认大小 1,073,741,824，即1G，太大了
max_binlog_size=100M
# 设置二进制日志缓存的大小为 20MB
binlog_cache_size = 20971520


# 【慢查询SQL日志】
# 打开慢查询SQL日志
slow_query_log=1
# 慢查询SQL日志文件
slow_query_log_file=/var/log/mysql/slow.log
# 把未使用到索引的SQL记录到慢查询日志
log_queries_not_using_indexes=1
# 定义慢查询的阈值，单位：秒，超过这个值则会被记录到慢查询日志
long_query_time=10

```

- `/etc/mysql/conf.d/innodb.cnf`（InnoDB配置）

```conf
[mysqld]
# MyISAM表索引缓冲区大小
key_buffer_size=16M

# InnoDB存储引擎缓冲池大小
innodb_buffer_pool_size=256M

innodb_buffer_pool_instances=50

# 设置InnoDB日志文件的大小
innodb_log_file_size=

query_cache_type：设置查询缓存类型。

# 设置查询缓存的大小
query_cache_size=16M
```

使用 MySQL 的性能监控工具如 `SHOW STATUS;` 和 `SHOW VARIABLES;` 来监视数据库状态。


- `/etc/mysql/conf.d/connect.cnf`（连接配置）

```conf
[mysqld]
# 设置最大连接数限制，默认为 151
max_connections=100

# 设置连接失败的最大次数
max_connect_errors=100

# 线程缓存大小
thread_cache_size=8



```





# 安装 MySQL

## 在 Debian 上安装 MySQL

## 在 Docker 上安装 MySQL

- 查看 MySQL 镜像版本

Docker Hub

https://hub.docker.com/_/mysql

https://hub.docker.com/_/mysql/tags

- 拉取 MySQL 镜像

```shell
docker pull mysql:9.0.0
```

- 查看本地镜像

```shell
docker images
```

- 运行 MySQL 容器

```shell
docker run \
-id \
--name mysql \
-p 3306:3306 \
-v /opt/mysql/conf:/etc/mysql/conf.d \
-v /opt/mysql/data:/var/lib/mysql \
-v /opt/mysql/log:/var/log/mysql \
-e TZ=Asia/Shanghai \
-e MYSQL_ROOT_PASSWORD=root \
--log-opt max-size=10m \
--log-opt max-file=1 \
-t mysql:9.0.0

# 说明：

# -p <host-port>:<container-port>

# -v <host-path>:<container-path>

# -v /opt/mysql/conf:/etc/mysql/conf.d
# 将主机 /opt/mysql/conf 目录挂载到容器的 /etc/mysql/conf.d

# -e MYSQL_ROOT_PASSWORD=123456
# 设置 MySQL 服务 root 用户的密码为 root

# --log-opt max-size=10m
# 指定日志文件的最大大小。当日志文件达到指定大小时，Docker 会自动进行日志文件的切割和轮转。
# 这会将容器的日志文件大小限制在 10MB，超过这个大小就会进行切割。

# --log-opt max-file=1
# 指定最大日志文件数目。这个选项设置允许保留的日志文件数量，超过指定数目的日志文件将被删除。
# 这会保留最多 1 个旧的日志文件，超过则会删除更早的日志文件。
```

- 查看是否安装成功

```shell
docker ps

docker exec -it -u root mysql /bin/bash

mysql -h localhost -P 3306 -u root -p
```

# Spring Boot 配置

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
    # 数据库连接URL，指定了数据库类型、地址、端口和数据库名
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
