package org.xiangqian.monolithic.mysql.binlog.service;

import org.xiangqian.monolithic.mysql.binlog.MysqlBinlogDmlListener;
import org.xiangqian.monolithic.mysql.binlog.MysqlBinlogDmlSubscribe;

/**
 * @author xiangqian
 * @date 21:53 2024/06/27
 */
@MysqlBinlogDmlSubscribe(mysql = "example", database = "sys", tables = {"user", "role"})
public interface UserService extends MysqlBinlogDmlListener {
}
