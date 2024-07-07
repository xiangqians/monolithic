package org.xiangqian.monolithic.mysql.binlog;

import org.xiangqian.monolithic.common.util.MapWrapper;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * @author xiangqian
 * @date 19:29 2024/06/27
 */
public class MysqlBinlogDmlRow extends MapWrapper {

    private MysqlColumns mysqlColumns;
    private Serializable[] row;

    public MysqlBinlogDmlRow(MysqlColumns mysqlColumns, Serializable[] row) {
        super(null);
        this.mysqlColumns = mysqlColumns;
        this.row = row;
    }

    @Override
    public Object get(Object key) {
        Integer index = mysqlColumns.get(key.toString());
        if (index != null) {
            return row[index];
        }
        return null;
    }

    @Override
    public LocalDateTime getLocalDateTime(String key, LocalDateTime defaultValue) {
        Object value = get(key);
        if (value == null) {
            return defaultValue;
        }

        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }

        if (value instanceof Number) {
            Number number = (Number) value;
            long l = number.longValue();
            if (l < 10000000000L) {
                l *= 1000;
            }
            return LocalDateTime.ofInstant(Instant.ofEpochMilli(l), ZoneOffset.UTC);
        }

        return defaultValue;
    }

    @Override
    public Date getDate(String key, Date defaultValue) {
        LocalDateTime dateTime = getLocalDateTime(key);
        if (dateTime != null) {
            return Date.from(dateTime.atZone(ZoneOffset.UTC).toInstant());
        }
        return defaultValue;
    }

}
