package org.xiangqian.monolithic.mysql.binlog;

import java.lang.annotation.*;

/**
 * MySQL 二进制日志 DML 订阅
 *
 * @author xiangqian
 * @date 23:28 2024/06/27
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface MysqlBinlogDmlSubscribe {

    /**
     * {@link MysqlProperties#getName()}
     *
     * @return
     */
    String mysql();

    /**
     * {@link MysqlDatabaseProperties#getName()}
     *
     * @return
     */
    String database();

    /**
     * {@link MysqlDatabaseProperties#getTables()}
     *
     * @return
     */
    String[] tables();

}



