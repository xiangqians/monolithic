package org.xiangqian.monolithic.mysql.binlog.service;

import org.xiangqian.monolithic.mysql.binlog.MysqlBinlogDmlListener;
import org.xiangqian.monolithic.mysql.binlog.MysqlBinlogDmlSubscribe;

/**
 * @author xiangqian
 * @date 17:55 2024/06/27
 */
@MysqlBinlogDmlSubscribe(mysql = "example", database = "pay", tables = {"*"})
public interface PayService extends MysqlBinlogDmlListener {
}
