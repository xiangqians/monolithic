package org.xiangqian.monolithic.mysql.binlog;

import lombok.Data;

import java.util.List;

/**
 * 数据库配置
 *
 * @author xiangqian
 * @date 23:01 2024/06/27
 */
@Data
public class MysqlDatabaseProperties {

    /**
     * 数据库名
     */
    private String name;

    /**
     * 数据表
     */
    private List<String> tables;

}
