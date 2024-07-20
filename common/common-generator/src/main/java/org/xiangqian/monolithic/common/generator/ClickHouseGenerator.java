package org.xiangqian.monolithic.common.generator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * ClickHouse 生成器
 *
 * @author xiangqian
 * @date 21:56 2024/07/16
 */
public class ClickHouseGenerator extends AbsGenerator {

    /**
     * @param host   数据库主机
     * @param port   数据库端口
     * @param user   数据库用户名
     * @param passwd 数据库密码
     */
    public ClickHouseGenerator(String host, Integer port, String user, String passwd) throws ClassNotFoundException {
        super(String.format("jdbc:clickhouse://%s:%s/default", host, port), user, passwd);

        // 加载数据库驱动
        Class.forName("com.clickhouse.jdbc.ClickHouseDriver");
    }

    @Override
    protected String getSql(String database, String[] tables) {
        return String.format("SELECT t.name TABLE_NAME, t.comment TABLE_COMMENT, c.name COLUMN_NAME, c.type COLUMN_TYPE, c.comment COLUMN_COMMENT" +
                        " FROM system.tables t" +
                        " JOIN system.columns c ON c.database = t.database AND c.table = t.name" +
                        " WHERE t.database = '%s' AND t.name IN (%s)",
                database, Arrays.stream(tables).map(table -> String.format("'%s'", table)).collect(Collectors.joining(", ")));
    }

    @Override
    protected String getEscapeTableName(String tableName) {
        return tableName;
    }

    @Override
    protected String getEscapeColumnName(String columnName) {
        return columnName;
    }

    @Override
    protected Class<?> getColumnType(String columnType) {
        // INT8 / UINT8
        if (isMatchAnyWords(columnType, "INT8", "UINT8")) {
            return Byte.class;
        }

        // INT16 / UINT16
        if (isMatchAnyWords(columnType, "INT16", "UINT16")) {
            return Short.class;
        }

        // INT32 / UINT32
        if (isMatchAnyWords(columnType, "INT32", "UINT32")) {
            return Integer.class;
        }

        // INT64 / UINT64
        if (isMatchAnyWords(columnType, "INT64", "UINT64")) {
            return Long.class;
        }

        // STRING
        if (isMatchAnyWords(columnType, "STRING")) {
            return String.class;
        }

        // DATE
        if (isMatchAnyWords(columnType, "DATE")) {
            return LocalDate.class;
        }

        // DATETIME
        if (isMatchAnyWords(columnType, "DATETIME")) {
            return LocalDateTime.class;
        }

        System.err.println(columnType);
        return null;
    }

}
