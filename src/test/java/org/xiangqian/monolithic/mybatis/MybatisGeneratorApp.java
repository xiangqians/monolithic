package org.xiangqian.monolithic.mybatis;

/**
 * 代码生成Application
 *
 * @author xiangqian
 * @date 19:23 2023/05/19
 */
public class MybatisGeneratorApp {

    public static void main(String[] args) {
        // generator
        MybatisGenerator generator = new MybatisGenerator.Builder()
                .author("xiangqian")
                .profile("dev")
                .datasource("master")
                .biz("user")
                .tables("user", "role")
                .build();

        // execute
        generator.execute();
    }

}
