package org.xiangqian.monolithic.common.clickhouse;

import org.xiangqian.monolithic.common.generator.ClickHouseGenerator;
import org.xiangqian.monolithic.common.generator.Generator;

/**
 * @author xiangqian
 * @date 21:54 2024/07/12
 */
public class GeneratorTest {

    // 设置环境变量：
    // HOST=localhost
    // PORT=9000
    // USER=default
    // PASSWD=default
    public static void main(String[] args) throws Exception {
        // 创建生成器
        String host = System.getenv("HOST");
        Integer port = Integer.parseInt(System.getenv("PORT"));
        String user = System.getenv("USER");
        String passwd = System.getenv("PASSWD");
        System.out.printf("数据库" +
                        "\n\thost\t: %s" +
                        "\n\tport\t: %s" +
                        "\n\tuser\t: %s" +
                        "\n\tpasswd\t: %s\n\n",
                host, port, user, passwd);
        Generator generator = new ClickHouseGenerator(host, port, user, passwd);

        // 生成模板
        generator.generateTemplate("org.xiangqian.monolithic.common.clickhouse",
                "sys",
                "xiangqian",
                "market",
                "bulk_goods", "goods"
        );

//        generator.generateInsert("monolithic", "sys_tenant", "entity");
//        generator.generateUpdate("monolithic", "sys_tenant", "entity");
//        generator.generateSelect("monolithic", "sys_tenant", "t", "entity");
    }


}
