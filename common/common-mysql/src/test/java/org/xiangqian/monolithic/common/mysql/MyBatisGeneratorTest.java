package org.xiangqian.monolithic.common.mysql;

import com.baomidou.mybatisplus.annotation.DbType;
import org.xiangqian.monolithic.common.generator.MybatisGenerator;

import java.io.IOException;

/**
 * @author xiangqian
 * @date 19:52 2023/05/19
 */
public class MyBatisGeneratorTest {

    // 环境变量：
    // HOST=localhost
    // PORT=3306
    // DATABASE=monolithic
    // USER=root
    // PASSWD=root
    public static void main(String[] args) throws Exception {
        execute("sys",
                "xiangqian",
                "sys_tenant", "sys_user", "sys_authority_group", "sys_authority", "sys_role", "sys_role_authority", "sys_dict_type", "sys_dict_item", "sys_log");
    }

    private static void execute(String moduleName, String author, String... tables) throws IOException {
        // 加载数据库驱动
//        Class.forName("com.mysql.cj.jdbc.Driver");

        String host = System.getenv("HOST");
        String port = System.getenv("PORT");
        String database = System.getenv("DATABASE");
        String user = System.getenv("USER");
        String passwd = System.getenv("PASSWD");
        System.out.printf("数据源" +
                        "\n\thost\t: %s" +
                        "\n\tport\t: %s" +
                        "\n\tdatabase: %s" +
                        "\n\tuser\t: %s" +
                        "\n\tpasswd\t: %s\n\n",
                host, port, database, user, passwd);

        MybatisGenerator.execute("org.xiangqian.monolithic.common.mysql",
                DbType.MYSQL,
                host, port, database, user, passwd,
                moduleName,
                author,
                tables);
    }

}
