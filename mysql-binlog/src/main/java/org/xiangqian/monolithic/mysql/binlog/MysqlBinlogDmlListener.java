package org.xiangqian.monolithic.mysql.binlog;

/**
 * MySQL 二进制日志监听器
 *
 * @author xiangqian
 * @date 23:09 2024/06/27
 */
public interface MysqlBinlogDmlListener {

    /**
     * 当新增时
     *
     * @param table 数据表
     * @param rows  行数据集合
     */
    void onInsert(String table, Iterable<MysqlBinlogDmlRow> rows);

    /**
     * 当修改时
     *
     * @param table 数据表
     * @param rows  行数据集合
     *              before=MysqlBinlogDmlRow[0] 修改前
     *              after =MysqlBinlogDmlRow[1] 修改后
     */
    void onUpdate(String table, Iterable<MysqlBinlogDmlRow[]> rows);

    /**
     * 当删除时
     *
     * @param table 数据表
     * @param rows  行数据集合
     */
    void onDelete(String table, Iterable<MysqlBinlogDmlRow> rows);

}