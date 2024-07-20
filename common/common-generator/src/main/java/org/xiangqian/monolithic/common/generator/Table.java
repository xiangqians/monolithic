package org.xiangqian.monolithic.common.generator;

import lombok.Data;
import org.apache.commons.collections4.CollectionUtils;
import org.xiangqian.monolithic.common.util.NamingUtil;

import java.util.List;
import java.util.Set;

/**
 * 数据表信息
 *
 * @author xiangqian
 * @date 21:25 2024/07/16
 */
@Data
public class Table {

    /**
     * 数据表名
     */
    private String name;

    /**
     * 数据表名转义名称
     */
    private String escapeName;

    /**
     * 类名
     */
    private String className;

    /**
     * 数据表注释
     */
    private String comment;

    /**
     * 字段信息集合
     */
    private List<Column> columns;

    /**
     * 字段类型集合
     */
    private Set<Class<?>> columnTypes;

    public Table(String name, String escapeName, String comment) {
        this.name = name;
        this.escapeName = escapeName;
        this.className = NamingUtil.lowerUnderscoreConvertToUpperCamel(name);
        this.comment = comment;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder("Table(").append("name=").append(name)
                .append(", ").append("className=").append(className)
                .append(", ").append("comment=").append(comment)
                .append(")");

        if (CollectionUtils.isNotEmpty(columns)) {
            builder.append('\n');
            for (Column column : columns) {
                builder.append('\t').append(column).append('\n');
            }
        }

        return builder.toString();
    }

}
