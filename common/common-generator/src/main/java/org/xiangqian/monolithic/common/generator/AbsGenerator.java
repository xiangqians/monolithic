package org.xiangqian.monolithic.common.generator;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.xiangqian.monolithic.common.util.NamingUtil;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author xiangqian
 * @date 21:13 2024/07/16
 */
public abstract class AbsGenerator implements Generator {

    private String url;
    private String user;
    private String passwd;

    /**
     * @param user   数据库用户名
     * @param passwd 数据库密码
     */
    public AbsGenerator(String url, String user, String passwd) {
        this.url = url;
        this.user = user;
        this.passwd = passwd;
    }

    @Override
    public void generateTemplate(String basePkg, String moduleName, String author, String database, String... tables) throws Exception {
        List<Table> tableList = getTables(moduleName, database, tables);
        if (CollectionUtils.isEmpty(tableList)) {
            return;
        }

//        tableList.forEach(System.out::println);

        // 初始化模板引擎
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.setProperty("resource.loader", "class");
        velocityEngine.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        velocityEngine.init();

        // 创建模板上下文
        VelocityContext velocityContext = new VelocityContext();

        // 模板属性
        velocityContext.put("basePkg", basePkg);
        velocityContext.put("moduleName", moduleName);
        velocityContext.put("author", author);
        velocityContext.put("date", DateTimeFormatter.ofPattern("HH:mm yyyy/MM/dd").format(LocalDateTime.now()));
        Table table = tableList.get(0);
        velocityContext.put("table", table);

        String[] names = new String[]{"template/entity.java.vm", "template/mapper.java.vm", "template/mapper.xml.vm"};

        // 获取模板
        Template template = velocityEngine.getTemplate("template/entity.java.vm");

        // 处理模板
        StringWriter writer = new StringWriter();
        template.merge(velocityContext, writer);

        // 输出生成的文本
        System.out.println(writer.toString());
    }

    @Override
    public void generateInsert(String database, String table, String name) throws Exception {
        List<Table> tables = getTables(null, database, new String[]{table});
        if (CollectionUtils.isEmpty(tables)) {
            return;
        }

        Table table1 = tables.get(0);
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<insert id=\"\">");
        xmlBuilder.append("\n\t").append("INSERT INTO ").append(table1.getName());

        if (name != null) {
            name += ".";
        } else {
            name = "";
        }

        List<Column> columns = table1.getColumns();
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

    @Override
    public void generateUpdate(String database, String table, String name) throws Exception {
        List<Table> tables = getTables(null, database, new String[]{table});
        if (CollectionUtils.isEmpty(tables)) {
            return;
        }

        Table table1 = tables.get(0);

        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<update id=\"\">");
        xmlBuilder.append("\n\t").append("UPDATE ").append(table1.getName());
        if (name != null) {
            name += ".";
        } else {
            name = "";
        }
        List<Column> columns = table1.getColumns();
        xmlBuilder.append("\n\t").append("<trim prefix=\"SET\" suffixOverrides=\",\">");
        for (Column column : columns) {
            xmlBuilder.append("\n\t\t").append("<if test=\"").append(name).append(column.getFieldName()).append(" != null\">")
                    .append(column.getName()).append(" = ").append("#{").append(name).append(column.getFieldName()).append("},")
                    .append("</if>");
        }
        xmlBuilder.append("\n\t").append("</trim>");
        xmlBuilder.append("\n\t").append("WHERE 1 = 2");
        xmlBuilder.append("\n").append("</update>");
        System.out.println(xmlBuilder);
    }

    @Override
    public void generateSelect(String database, String table, String alias, String name) throws Exception {
        List<Table> tables = getTables(null, database, new String[]{table});
        if (CollectionUtils.isEmpty(tables)) {
            return;
        }

        Table table1 = tables.get(0);

        List<Column> columns = table1.getColumns();
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<select id=\"\">");
        xmlBuilder.append("\n\t").append("SELECT ").append(columns.stream().map(column -> alias + "." + column.getName()).collect(Collectors.joining(", ")));
        xmlBuilder.append("\n\t").append("FROM ").append(table1.getName()).append(" ").append(alias);
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
     * 获取数据表结构 SQL
     *
     * @param database 数据库名
     * @param tables   数据表集合
     * @return
     */
    protected abstract String getSql(String database, String[] tables);

    /**
     * 获取数据表名转义名称
     *
     * @param tableName
     * @return
     */
    protected abstract String getEscapeTableName(String tableName);

    /**
     * 获取字段名转义名称
     *
     * @param columnName
     * @return
     */
    protected abstract String getEscapeColumnName(String columnName);

    /**
     * 获取字段类型
     *
     * @param columnType
     * @return
     */
    protected abstract Class<?> getColumnType(String columnType);

    private List<Table> getTables(String moduleName, String database, String[] tables) throws Exception {
        List<Table> tableList = getTables(database, tables);
        if (moduleName != null && CollectionUtils.isNotEmpty(tableList)) {
            String prefix = moduleName + "_";
            for (Table table : tableList) {
                String tableName = table.getName();
                if (tableName.startsWith(prefix)) {
                    tableName = tableName.substring(prefix.length());
                    table.setClassName(NamingUtil.lowerUnderscoreConvertToUpperCamel(tableName));
                }
            }
        }
        return tableList;
    }

    /**
     * 获取数据表集信息
     *
     * @param database 数据库名
     * @param tables   数据表集合
     * @return
     * @throws Exception
     */
    private List<Table> getTables(String database, String[] tables) throws Exception {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DriverManager.getConnection(url, user, passwd);

            String sql = getSql(database, tables);
            preparedStatement = connection.prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();
            Map<String, Table> tableMap = new HashMap<>(8, 1f);
            while (resultSet.next()) {
                String tableName = resultSet.getString("TABLE_NAME");
                String tableComment = resultSet.getString("TABLE_COMMENT");
                String columnName = resultSet.getString("COLUMN_NAME");
                Class<?> columnType = getColumnType(resultSet.getString("COLUMN_TYPE"));
                String columnComment = resultSet.getString("COLUMN_COMMENT");

                Table table = tableMap.get(tableName);
                if (table == null) {
                    table = new Table(tableName, getEscapeTableName(tableName), tableComment);
                    table.setColumns(new ArrayList<>(32));
                    table.setColumnTypes(new HashSet<>(16, 1f));
                    tableMap.put(tableName, table);
                }

                Column column = new Column(columnName, getEscapeColumnName(columnName), columnType, columnComment);
                List<Column> columns = table.getColumns();
                columns.add(column);

                Set<Class<?>> columnTypes = table.getColumnTypes();
                columnTypes.add(columnType);
            }

            return tableMap.values().stream().collect(Collectors.toList());
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (Exception e) {
            }

            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (Exception e) {
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * 是否匹配任意单个单词（忽略大小写）
     *
     * @param text
     * @param words
     * @return
     */
    protected boolean isMatchAnyWords(String text, String... words) {
        for (String word : words) {
            if (isMatchWord(text, word)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否匹配单个单词集（忽略大小写）
     *
     * @param text
     * @param words
     * @return
     */
    protected boolean isMatchWords(String text, String... words) {
        for (String word : words) {
            if (!isMatchWord(text, word)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 是否匹配单个单词（忽略大小写）
     *
     * @param text
     * @param word
     * @return
     */
    private boolean isMatchWord(String text, String word) {
        String regex = "\\b" + Pattern.quote(word) + "\\b";
        Pattern pattern = Pattern.compile(regex,
                // 忽略大小写
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(text);
        return matcher.find();
    }

}
