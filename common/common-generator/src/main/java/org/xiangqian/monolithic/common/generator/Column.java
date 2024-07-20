package org.xiangqian.monolithic.common.generator;

import lombok.Data;
import org.xiangqian.monolithic.common.util.NamingUtil;

/**
 * 字段信息
 *
 * @author xiangqian
 * @date 21:25 2024/07/16
 */
@Data
public class Column {

    /**
     * 字段名
     */
    private String name;

    /**
     * 字段名转义名称
     */
    private String escapeName;

    /**
     * 属性名
     */
    private String fieldName;

    /**
     * 字段类型
     */
    private Class<?> type;

    /**
     * 字段注释
     */
    private String comment;

    public Column(String name, String escapeName, Class<?> type, String comment) {
        this.name = name;
        this.escapeName = escapeName;
        this.fieldName = NamingUtil.lowerUnderscoreConvertToLowerCamel(name);
        this.type = type;
        this.comment = comment;
    }

}
