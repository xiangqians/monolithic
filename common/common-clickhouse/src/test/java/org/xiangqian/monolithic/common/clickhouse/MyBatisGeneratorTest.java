package org.xiangqian.monolithic.common.clickhouse;

import com.baomidou.mybatisplus.annotation.DbType;
import lombok.SneakyThrows;
import org.xiangqian.monolithic.common.generator.MybatisGenerator;

import java.io.IOException;

/**
 * @author xiangqian
 * @date 21:54 2024/07/12
 */
public class MyBatisGeneratorTest {

    // 环境变量：
    // HOST=localhost
    // PORT=9000
    // DATABASE=default
    // USER=default
    // PASSWD=default
    public static void main(String[] args) throws Exception {
        execute("sys",
                "xiangqian",
                "bulk_goods");
    }

    private static void execute(String moduleName, String author, String... tables) throws IOException {
        // 加载数据库驱动
//        Class.forName("com.clickhouse.jdbc.ClickHouseDriver");

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

        MybatisGenerator.execute("org.xiangqian.monolithic.common.clickhouse",
                DbType.CLICK_HOUSE,
                host, port, database, user, passwd,
                moduleName,
                author,
                tables);
    }

}
