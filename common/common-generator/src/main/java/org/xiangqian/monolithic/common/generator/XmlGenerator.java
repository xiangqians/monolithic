package org.xiangqian.monolithic.common.generator;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.apache.ibatis.annotations.Param;
import org.xiangqian.monolithic.common.util.naming.NamingLowerCamelUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author xiangqian
 * @date 19:00 2024/01/29
 */
public class XmlGenerator {

    /**
     * SELECT 语句
     *
     * @param type  Entity 类型
     * @param alias 数据表别名
     * @param name  参数名，{@link Param#value()}
     */
    public static void select(Class<?> type, String alias, String name) {
        Table table = getTable(type);
        List<Column> columns = table.getColumns();
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<select id=\"\">");
        xmlBuilder.append("\n\t").append("SELECT ").append(columns.stream().map(column -> alias + "." + column.getName()).collect(Collectors.joining(", ")));
        xmlBuilder.append("\n\t").append("FROM ").append(table.getName()).append(" ").append(alias);
        if (name != null) {
            name += ".";
        } else {
            name = "";
        }
        xmlBuilder.append("\n\t").append("<where>");
        for (Column column : columns) {
            xmlBuilder.append("\n\t\t").append("<if test=\"").append(name).append(column.getFieldName()).append(" != null\">")
                    .append("AND ").append(alias).append(".").append(column.getName()).append(" = ").append("#{").append(name).append(column.getFieldName()).append("}")
                    .append("</if>");
        }
        xmlBuilder.append("\n\t").append("</where>");
        xmlBuilder.append("\n").append("</select>");
        System.out.println(xmlBuilder);
    }

    /**
     * UPDATE 语句
     *
     * @param type Entity 类型
     * @param name 参数名，{@link Param#value()}
     */
    public static void update(Class<?> type, String name) {
        Table table = getTable(type);
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<update id=\"\">");
        xmlBuilder.append("\n\t").append("UPDATE ").append(table.getName());
        if (name != null) {
            name += ".";
        } else {
            name = "";
        }
        List<Column> columns = table.getColumns();
        xmlBuilder.append("\n\t").append("<trim prefix=\"SET\" suffixOverrides=\",\">");
        Column primaryKey = null;
        for (Column column : columns) {
            if (primaryKey == null && column.getIsPrimaryKey()) {
                primaryKey = column;
                continue;
            }
            xmlBuilder.append("\n\t\t").append("<if test=\"").append(name).append(column.getFieldName()).append(" != null\">")
                    .append(column.getName()).append(" = ").append("#{").append(name).append(column.getFieldName()).append("},")
                    .append("</if>");
        }
        xmlBuilder.append("\n\t").append("</trim>");
        if (primaryKey != null) {
            xmlBuilder.append("\n\t").append("WHERE ").append(primaryKey.getName()).append(" = ").append("#{").append(name).append(primaryKey.getFieldName()).append("}");
        } else {
            xmlBuilder.append("\n\t").append("WHERE 1 = 2");
        }
        xmlBuilder.append("\n").append("</update>");
        System.out.println(xmlBuilder);
    }

    /**
     * INSERT 语句
     *
     * @param type Entity 类型
     * @param name 参数名，{@link Param#value()}
     */
    public static void insert(Class<?> type, String name) {
        Table table = getTable(type);
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<insert id=\"\">");
        xmlBuilder.append("\n\t").append("INSERT INTO ").append(table.getName());
        if (name != null) {
            name += ".";
        } else {
            name = "";
        }
        List<Column> columns = table.getColumns();
        xmlBuilder.append("\n\t").append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        for (Column column : columns) {
            xmlBuilder.append("\n\t\t").append("<if test=\"").append(name).append(column.getFieldName()).append(" != null\">").append(column.getName()).append(",</if>");
        }
        xmlBuilder.append("\n\t").append("</trim>");
        xmlBuilder.append("\n\t").append("<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
        for (Column column : columns) {
            xmlBuilder.append("\n\t\t").append("<if test=\"").append(name).append(column.getFieldName()).append(" != null\">")
                    .append("#{").append(name).append(column.getFieldName()).append("},")
                    .append("</if>");
        }
        xmlBuilder.append("\n\t").append("</trim>");
        xmlBuilder.append("\n").append("</insert>");
        System.out.println(xmlBuilder);
    }

    /**
     * 获取数据表信息
     *
     * @param type
     * @return
     */
    private static Table getTable(Class<?> type) {
        TableName tableName = type.getAnnotation(TableName.class);
        Table table = new Table(tableName.value());

        Field[] fields = type.getDeclaredFields();
        List<Column> columns = new ArrayList<>(fields.length);
        for (Field field : fields) {
            int modifiers = field.getModifiers();
            TableField tableField = null;
            if (Modifier.isStatic(modifiers)
                    || Modifier.isFinal(modifiers)
                    || ((tableField = field.getAnnotation(TableField.class)) != null && !tableField.exist())) {
                continue;
            }

            String fieldName = field.getName();
            String columnName = Optional.ofNullable(tableField).map(TableField::value).orElse(null);
            if (columnName == null) {
                columnName = NamingLowerCamelUtil.convToLowerUnderscore(fieldName);
            }
            columns.add(new Column(field.isAnnotationPresent(TableId.class), fieldName, columnName));
        }
        table.setColumns(columns);

        return table;
    }

    /**
     * 数据表信息
     */
    @Data
    private static class Table {
        /**
         * 数据表名
         */
        private String name;

        /**
         * 字段信息集合
         */
        private List<Column> columns;

        public Table(String name) {
            this.name = name;
        }
    }

    /**
     * 字段信息
     */
    @Data
    private static class Column {
        /**
         * 是否是主键
         */
        private Boolean isPrimaryKey;

        /**
         * java属性名
         */
        private String fieldName;

        /**
         * 字段名
         */
        private String name;

        public Column(Boolean isPrimaryKey, String fieldName, String name) {
            this.isPrimaryKey = isPrimaryKey;
            this.fieldName = fieldName;
            this.name = name;
        }
    }

}
