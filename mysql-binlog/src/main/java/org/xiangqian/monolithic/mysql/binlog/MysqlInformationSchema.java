package org.xiangqian.monolithic.mysql.binlog;

import lombok.SneakyThrows;

import java.io.Closeable;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * MySQL information_schema
 *
 * @author xiangqian
 * @date 23:56 2024/06/27
 */
public class MysqlInformationSchema implements Closeable {

    /**
     * 主机
     */
    private String host;

    /**
     * 端口
     */
    private Integer port;

    /**
     * 用户
     */
    private String user;

    /**
     * 密码
     */
    private String passwd;

    private Connection connection;

    public MysqlInformationSchema(String host, Integer port, String user, String passwd) {
        this.host = host;
        this.port = port;
        this.user = user;
        this.passwd = passwd;
    }

    private PreparedStatement prepareStatement(String sql) throws ClassNotFoundException, SQLException {
        if (connection == null) {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = String.format("jdbc:mysql://%s:%s/information_schema?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true", host, port);
            connection = DriverManager.getConnection(url, user, passwd);
        }
        return connection.prepareStatement(sql);
    }

    private void close(PreparedStatement preparedStatement, ResultSet resultSet) throws SQLException {
        try {
            if (resultSet != null) {
                resultSet.close();
            }
        } finally {
            if (preparedStatement != null) {
                preparedStatement.close();
            }
        }
    }

    public List<String> getTables(String database) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = prepareStatement("SELECT TABLE_NAME, TABLE_COMMENT FROM `TABLES` WHERE TABLE_SCHEMA = ?");
            preparedStatement.setObject(1, database);

            resultSet = preparedStatement.executeQuery();
            List<String> tables = null;
            while (resultSet.next()) {
                if (tables == null) {
                    tables = new ArrayList<>(8);
                }
                String table = resultSet.getString("TABLE_NAME");
                tables.add(table);
            }
            return tables;
        } finally {
            close(preparedStatement, resultSet);
        }
    }

    public List<String> getColumns(String database, String table) throws ClassNotFoundException, SQLException {
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            preparedStatement = prepareStatement("SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_SCHEMA = ? AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION");
            preparedStatement.setObject(1, database);
            preparedStatement.setObject(2, table);

            resultSet = preparedStatement.executeQuery();
            List<String> columns = null;
            while (resultSet.next()) {
                if (columns == null) {
                    columns = new ArrayList<>(8);
                }
                String column = resultSet.getString("COLUMN_NAME");
                columns.add(column);
            }
            return columns;
        } finally {
            close(preparedStatement, resultSet);
        }
    }


    @Override
    @SneakyThrows
    public void close() throws IOException {
        if (connection != null) {
            connection.close();
            connection = null;
        }
    }

}
