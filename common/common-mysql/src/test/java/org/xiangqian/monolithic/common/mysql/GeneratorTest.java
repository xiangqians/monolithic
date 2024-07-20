package org.xiangqian.monolithic.common.mysql;

import org.xiangqian.monolithic.common.generator.Generator;
import org.xiangqian.monolithic.common.generator.MysqlGenerator;

/**
 * @author xiangqian
 * @date 19:52 2023/05/19
 */
public class GeneratorTest {

    // 设置环境变量：
    // HOST=localhost
    // PORT=3306
    // USER=root
    // PASSWD=root
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
        Generator generator = new MysqlGenerator(host, port, user, passwd);

        // 生成模板
        generator.generateTemplate("org.xiangqian.monolithic.common.mysql",
                "sys",
                "xiangqian",
                "monolithic",
                "sys_tenant"//, "sys_user" //, "sys_authority_group", "sys_authority", "sys_role", "sys_role_authority", "sys_dict_type", "sys_dict_item", "sys_log"
        );

//        generator.generateInsert("monolithic", "sys_tenant", "entity");
//        generator.generateUpdate("monolithic", "sys_tenant", "entity");
//        generator.generateSelect("monolithic", "sys_tenant", "t", "entity");
    }

}
