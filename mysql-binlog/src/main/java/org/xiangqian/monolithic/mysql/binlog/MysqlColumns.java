package org.xiangqian.monolithic.mysql.binlog;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * MySQL 列（字段）信息集合
 *
 * @author xiangqian
 * @date 22:58 2024/06/27
 */
@Data
public class MysqlColumns {

    private Map<String, Integer> map;

    public MysqlColumns(int capacity) {
        this.map = new HashMap<>(capacity, 1f);
    }

    /**
     * 获取列（字段）索引
     *
     * @param column
     * @return
     */
    public Integer get(String column) {
        return map.get(column);
    }

    /**
     * 添加列（字段）
     *
     * @param column 列（字段）名
     * @param index  列（字段）索引，从 0 开始递增
     */
    public void add(String column, Integer index) {
        map.put(column, index);
    }

}
