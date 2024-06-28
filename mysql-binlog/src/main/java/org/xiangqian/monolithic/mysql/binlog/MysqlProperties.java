package org.xiangqian.monolithic.mysql.binlog;

import lombok.Data;

import java.util.List;

/**
 * MySQL配置
 *
 * @author xiangqian
 * @date 22:57 2024/06/27
 */
@Data
public class MysqlProperties {

    /**
     * 名称（唯一）
     */
    private String name;

    /**
     * MySQL 服务器的唯一标识符，每个服务器应具有唯一的 ID
     */
    private Long serverId;

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

    /**
     * 数据库
     */
    private List<MysqlDatabaseProperties> databases;

}
