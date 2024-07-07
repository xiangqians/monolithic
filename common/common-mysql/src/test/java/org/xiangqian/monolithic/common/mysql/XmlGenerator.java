package org.xiangqian.monolithic.common.mysql;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.SneakyThrows;
import org.xiangqian.monolithic.common.mysql.sched.entity.TaskRecordEntity;
import org.xiangqian.monolithic.common.mysql.sys.entity.LogEntity;
import org.xiangqian.monolithic.common.util.NamingUtil;

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

    @SneakyThrows
    public static void main(String[] args) {
//        select(TaskRecordEntity.class, "tr", "entity");
        select(LogEntity.class, "l", "logEntity");
    }

    private static void select(Class<?> type, String alias, String name) {
        Table table = getTable(type);
        List<Column> columns = table.getColumns();
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<select id=\"\">");
        xmlBuilder.append("\n\t").append("SELECT ").append(columns.stream().map(column -> getAlias(alias) + column.getName()).collect(Collectors.joining(", ")));
        xmlBuilder.append("\n\t").append("FROM ").append(table.getName()).append(" ").append(alias);

        xmlBuilder.append("\n\t").append("<where>");
        for (Column column : columns) {
            xmlBuilder.append("\n\t\t").append("<if test=\"").append(getName(name)).append(column.getFieldName()).append(" != null\">").append("AND ").append(getAlias(alias)).append(column.getName()).append(" = ").append("#{").append(getName(name)).append(column.getFieldName()).append("}").append("</if>");
        }
        xmlBuilder.append("\n\t").append("</where>");

        xmlBuilder.append("\n").append("</select>");
        System.out.println(xmlBuilder);
    }

    private static void update(Class<?> type) {
        Table table = getTable(type);
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<update id=\"\">");
        xmlBuilder.append("\n\t").append("UPDATE ").append(table.getName());

        List<Column> columns = table.getColumns();
        xmlBuilder.append("\n\t").append("<trim prefix=\"SET\" suffixOverrides=\",\">");
        for (Column column : columns) {
            xmlBuilder.append("\n\t\t").append("<if test=\"").append(column.getFieldName()).append(" != null\">").append(column.getName()).append(" = ").append("#{").append(column.getFieldName()).append("}").append(",</if>");
        }
        xmlBuilder.append("\n\t").append("</trim>");

        xmlBuilder.append("\n\t").append("WHERE id = #{id}");
        xmlBuilder.append("\n").append("</update>");
        System.out.println(xmlBuilder);
    }

    private static void insert(Class<?> type) {
        Table table = getTable(type);
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<insert id=\"\">");
        xmlBuilder.append("\n\t").append("INSERT INTO ").append(table.getName());

        List<Column> columns = table.getColumns();

        xmlBuilder.append("\n\t").append("<trim prefix=\"(\" suffix=\")\" suffixOverrides=\",\">");
        for (Column column : columns) {
            xmlBuilder.append("\n\t\t").append("<if test=\"").append(column.getFieldName()).append(" != null\">").append(column.getName()).append(",</if>");
        }
        xmlBuilder.append("\n\t").append("</trim>");

        xmlBuilder.append("\n\t").append("<trim prefix=\"VALUES (\" suffix=\")\" suffixOverrides=\",\">");
        for (Column column : columns) {
            xmlBuilder.append("\n\t\t").append("<if test=\"").append(column.getFieldName()).append(" != null\">#{").append(column.getFieldName()).append("},</if>");
        }
        xmlBuilder.append("\n\t").append("</trim>");

        xmlBuilder.append("\n").append("</insert>");
        System.out.println(xmlBuilder);
    }

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
                    || (tableField = field.getAnnotation(TableField.class)) != null && !tableField.exist()) {
                continue;
            }

            String fieldName = field.getName();
            String columnName = Optional.ofNullable(tableField).map(TableField::value).orElse(null);
            if (columnName == null) {
                columnName = NamingUtil.LowerCamel.convToLowerUnderscore(fieldName);
            }
            columns.add(new Column(fieldName, columnName));
        }
        table.setColumns(columns);

        return table;
    }

    private static String getName(String name) {
        if (name == null) {
            return "";
        }
        return name + ".";
    }

    private static String getAlias(String alias) {
        if (alias == null) {
            return "";
        }
        return alias + ".";
    }

    @Data
    private static class Table {
        private String name;
        private List<Column> columns;

        public Table(String name) {
            this.name = name;
        }
    }

    @Data
    private static class Column {
        private String fieldName;
        private String name;

        public Column(String fieldName, String name) {
            this.fieldName = fieldName;
            this.name = name;
        }
    }

}
