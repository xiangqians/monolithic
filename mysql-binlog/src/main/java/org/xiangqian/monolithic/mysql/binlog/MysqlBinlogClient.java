package org.xiangqian.monolithic.mysql.binlog;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.*;
import com.github.shyiko.mysql.binlog.event.deserialization.EventDeserializer;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

/**
 * @author xiangqian
 * @date 23:10 2024/06/27
 */
@Slf4j
public class MysqlBinlogClient implements BinaryLogClient.EventListener, Runnable, Closeable {

    private ApplicationContext applicationContext;
    private MysqlProperties mysqlProperties;
    private BinaryLogClient binaryLogClient;

    private Set<String> databases;

    /**
     * Map<database.table, {@link MysqlColumns}>
     */
    private Map<String, MysqlColumns> mysqlColumnsMap;

    /**
     * Map<{@link com.github.shyiko.mysql.binlog.event.TableMapEventData#getTableId()}, database.table>
     */
    private Map<Long, String> databaseTableMap;

    private Map<String, List<MysqlBinlogDmlListener>> mysqlBinlogDmlListenersMap;

    public MysqlBinlogClient(ApplicationContext applicationContext, MysqlProperties mysqlProperties) {
        this.applicationContext = applicationContext;
        this.mysqlProperties = mysqlProperties;
    }

    /**
     * CREATE：用于创建新的数据库对象，如表、索引、视图等
     *
     * @param database
     * @param sql
     */
    private void onDdlCreate(String database, String sql) {
    }

    /**
     * ALTER：用于修改已有的数据库对象的结构，例如修改表的列、添加约束等
     *
     * @param database
     * @param sql
     */
    private void onDdlAlter(String database, String sql) {
    }

    /**
     * DROP：用于删除数据库中的对象，如表、索引、视图等
     *
     * @param database
     * @param sql
     */
    private void onDdlDrop(String database, String sql) {
    }

    /**
     * TRUNCATE：用于删除表中的所有数据，但保留表的结构
     *
     * @param database
     * @param sql
     */
    private void onDdlTruncate(String database, String sql) {
    }

    /**
     * RENAME：用于重命名数据库对象，如表、列等
     *
     * @param database
     * @param sql
     */
    private void onDdlRename(String database, String sql) {
    }

    /**
     * SELECT：用于从一个或多个表中检索数据
     *
     * @param database
     * @param sql
     */
    private void onDqlSelect(String database, String sql) {
    }

    /**
     * INSERT：用于向数据库表中插入新的行
     *
     * @param tableId
     * @param rows
     */
    private void onDmlInsert(Long tableId, List<Serializable[]> rows) {
    }

    /**
     * UPDATE：用于更新数据库表中已有行的数据
     *
     * @param updateRowsEventData
     */
    private void onDml(UpdateRowsEventData updateRowsEventData) {
        String databaseTable = databaseTableMap.get(updateRowsEventData.getTableId());
        if (databaseTable == null) {
            return;
        }

        MysqlColumns mysqlColumns = mysqlColumnsMap.get(databaseTable);
        if (mysqlColumns == null) {
            return;
        }

        List<Map.Entry<Serializable[], Serializable[]>> rows = updateRowsEventData.getRows();
        for (Map.Entry<Serializable[], Serializable[]> row : rows) {
            Serializable[] before = row.getKey();
            Serializable[] after = row.getValue();
        }
    }

    /**
     * DELETE：用于从数据库表中删除行
     *
     * @param tableId
     * @param rows
     */
    private void onDmlDelete(Long tableId, List<Serializable[]> rows) {
    }

    @Override
    public void onEvent(Event event) {
        EventHeader eventHeader = event.getHeader();
        EventType eventType = eventHeader.getEventType();

        // 不计入position更新（断点续传）的事件
        //  FORMAT_DESCRIPTION类型为binlog起始时间
        if (eventType == EventType.FORMAT_DESCRIPTION
                // HEARTBEAT为心跳检测事件，不会写入master（source）的binlog，记录该事件的position会导致重启时报错
                || eventType == EventType.FORMAT_DESCRIPTION) {
            return;
        }

        // 事件数据
        EventData eventData = event.getData();

        // 端点续传事件
        // 当binlog服务上线之后，服务就会在log文件的最新position处进行监听。
        // 格式：Connected to localhost:3306 at mysql-bin.000013/1075 (sid:65535, cid:7)
        // 但由于binlog服务下线，重新启动后，默认又开始在最新position处进行监听，会丢失部分binlog事件。
        // 所以，每次事件均需要记录当前的fileName和position，重新建立client端时，使用记录的fileName和position。
        // 需要监听 EventType.ROTATE 事件。
        // EventType.ROTATE：当binlog文件的大小达到max_binlog_size的值或者执行flush logs命令时，binlog会发生切换，这个时候会在当前的binlog日志添加一个ROTATE_EVENT事件，用于指定下一个日志的名称和位置。
        if (eventData instanceof RotateEventData) {
            RotateEventData rotateEventData = (RotateEventData) eventData;
            String binlogFileName = rotateEventData.getBinlogFilename();
            long binlogPosition = rotateEventData.getBinlogPosition();
            log.debug("binlogFileName={}, binlogPosition={}", binlogFileName, binlogPosition);
            return;
        }

        if (eventHeader instanceof EventHeaderV4) {
            EventHeaderV4 eventHeaderV4 = (EventHeaderV4) eventHeader;
            long binlogPosition = eventHeaderV4.getPosition();
            log.debug("binlogPosition={}", binlogPosition);
        }

        // 数据表映射事件数据
        if (eventData instanceof TableMapEventData) {
            TableMapEventData tableMapEventData = (TableMapEventData) eventData;
            databaseTableMap.put(tableMapEventData.getTableId(), String.format("%s.%s", tableMapEventData.getDatabase(), tableMapEventData.getTable()));
            return;
        }

        // 查询事件数据
        if (eventData instanceof QueryEventData) {
            QueryEventData queryEventData = (QueryEventData) eventData;
            String database = queryEventData.getDatabase();
            if (!databases.contains(database)) {
                return;
            }

            String sql = queryEventData.getSql();
            if (StringUtils.isEmpty(sql)) {
                return;
            }


            // DDL语句事件::start

            // DDL（Data Definition Language，数据定义语言）语句用于定义、修改和删除数据库对象，例如表、索引、视图等

            if (StringUtils.startsWithIgnoreCase(sql, "CREATE")) {
                onDdlCreate(database, sql);
                return;
            }

            if (StringUtils.startsWithIgnoreCase(sql, "ALTER")) {
                onDdlAlter(database, sql);
                return;
            }

            if (StringUtils.startsWithIgnoreCase(sql, "DROP")) {
                onDdlDrop(database, sql);
                return;
            }

            if (StringUtils.startsWithIgnoreCase(sql, "TRUNCATE")) {
                onDdlTruncate(database, sql);
                return;
            }

            if (StringUtils.startsWithIgnoreCase(sql, "RENAME")) {
                onDdlRename(database, sql);
                return;
            }

            // DDL语句事件::end


            // DQL语句事件::start

            // DQL（Data Query Language，数据查询语言）语句用于从数据库中检索数据。

            if (StringUtils.startsWithIgnoreCase(sql, "SELECT")) {
                onDqlSelect(database, sql);
                return;
            }

            // DQL语句事件::end

            return;
        }


        // DML语句事件::start

        // DML（Data Manipulation Language，数据操作语言）是 SQL 中的一类语言，用于操作数据库中的数据

        // insert
        if (eventData instanceof WriteRowsEventData) {
            WriteRowsEventData writeRowsEventData = (WriteRowsEventData) eventData;
            onDmlInsert(writeRowsEventData.getTableId(), writeRowsEventData.getRows());
            return;
        }

        // update
        if (eventData instanceof UpdateRowsEventData) {
            UpdateRowsEventData updateRowsEventData = (UpdateRowsEventData) eventData;
            onDml(updateRowsEventData.getTableId(), updateRowsEventData.getRows());
            return;
        }

        // delete
        if (eventData instanceof DeleteRowsEventData) {
            DeleteRowsEventData deleteRowsEventData = (DeleteRowsEventData) eventData;
            onDmlDelete(deleteRowsEventData.getTableId(), deleteRowsEventData.getRows());
            return;
        }

        // DML语句事件::end

//        log.warn("待处理事件：{}，{}", eventType, eventData);
    }

    @Override
    @SneakyThrows
    public void run() {
        MysqlInformationSchema mysqlInformationSchema = null;
        try {
            mysqlInformationSchema = new MysqlInformationSchema(mysqlProperties.getHost(), mysqlProperties.getPort(), mysqlProperties.getUser(), mysqlProperties.getPasswd());
            List<MysqlDatabaseProperties> mysqlDatabasePropertiesList = mysqlProperties.getDatabases();
            mysqlColumnsMap = new HashMap<>(mysqlDatabasePropertiesList.size(), 1f);
            databases = new HashSet<>(4, 1f);
            for (MysqlDatabaseProperties mysqlDatabaseProperties : mysqlDatabasePropertiesList) {
                String database = mysqlDatabaseProperties.getName();
                databases.add(database);

                // 获取数据表
                List<String> tables = mysqlDatabaseProperties.getTables();
                if ("*".equals(tables.get(0))) {
                    tables = mysqlInformationSchema.getTables(database);
                }

                // 获取列
                for (String table : tables) {
                    List<String> columns = mysqlInformationSchema.getColumns(database, table);
                    if (CollectionUtils.isNotEmpty(columns)) {
                        int size = columns.size();
                        MysqlColumns mysqlColumns = new MysqlColumns(size);
                        for (int i = 0; i < size; i++) {
                            String column = columns.get(i);
                            mysqlColumns.add(column, i);
                        }
                        mysqlColumnsMap.put(String.format("%s.%s", database, table), mysqlColumns);
                    }
                }
            }
            databaseTableMap = new HashMap<>(mysqlColumnsMap.size(), 1f);


            mysqlBinlogDmlListenersMap = new HashMap<>(8, 1f);

        } finally {
            IOUtils.closeQuietly(mysqlInformationSchema);
        }

        binaryLogClient = new BinaryLogClient(mysqlProperties.getHost(), mysqlProperties.getPort(), mysqlProperties.getUser(), mysqlProperties.getPasswd());

        // MySQL 服务器的唯一标识符，每个服务器应具有唯一的 ID
        binaryLogClient.setServerId(mysqlProperties.getServerId());

        // 设置二进制日志文件名称
//        binaryLogClient.setBinlogFilename();
        // 设置二进制日志文件位置
//        binaryLogClient.setBinlogPosition();

        // 事件反序列化器
        EventDeserializer eventDeserializer = new EventDeserializer();
        eventDeserializer.setCompatibilityMode(
                // 日期数据反序列化为时间戳（UTC时区）
                EventDeserializer.CompatibilityMode.DATE_AND_TIME_AS_LONG,
                // 字符、二进制数据反序列化为字节数组
                EventDeserializer.CompatibilityMode.CHAR_AND_BINARY_AS_BYTE_ARRAY
        );
        binaryLogClient.setEventDeserializer(eventDeserializer);

        // 添加事件监听器
        binaryLogClient.registerEventListener(this);

        // 连接
        binaryLogClient.connect();

        // 关闭连接
        close();
    }

    @Override
    public void close() throws IOException {
        if (binaryLogClient != null) {
            binaryLogClient.disconnect();
            binaryLogClient = null;
        }
    }

}
