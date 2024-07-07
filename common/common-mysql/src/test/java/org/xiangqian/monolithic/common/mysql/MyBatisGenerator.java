package org.xiangqian.monolithic.common.mysql;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import com.mysql.cj.jdbc.Driver;
import lombok.SneakyThrows;
import org.apache.commons.io.file.PathUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * MyBatis v3.5.3.1 代码生成器
 *
 * @author xiangqian
 * @date 19:52 2023/05/19
 */
public class MyBatisGenerator {

    // 环境变量：
    // HOST=localhost
    // PORT=3306
    // DATABASE=monolithic
    // USER=root
    // PASSWD=root
    @SneakyThrows
    public static void main(String[] args) {
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
        execute(host, port, database, user, passwd,
                "sys",
                "xiangqian",
                "sys_tenant", "sys_user", "sys_authority_group", "sys_authority", "sys_role", "sys_role_authority", "sys_dict_type", "sys_dict_item", "sys_log");
    }

    /**
     * 执行
     *
     * @param host       数据库主机
     * @param port       数据库端口
     * @param database   数据库名
     * @param user       数据库用户名
     * @param passwd     数据库密码
     * @param moduleName 模块名
     * @param author     作者
     * @param tables     数据表集合
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public static void execute(String host, String port, String database, String user, String passwd, String moduleName, String author, String... tables) throws ClassNotFoundException, IOException {
        // 加载数据库驱动
        // com.mysql.cj.jdbc.Driver
        String driverClassName = Driver.class.getName();
        Class.forName(driverClassName);

        // 数据库地址
        String url = String.format("jdbc:mysql://%s:%s/%s?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true", host, port, database);

        // 数据源配置
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(url, user, passwd)
                .dbQuery(new MySqlQuery())
                .typeConvert(new MySqlTypeConvert())
                .keyWordsHandler(new MySqlKeyWordsHandler())
                .build();

        // 输出目录
        Path outputDirPath = Paths.get(Thread.currentThread().getContextClassLoader().getResource("").getPath().substring(1)).getParent().resolve("generated-test-sources");
        if (Files.exists(outputDirPath)) {
            PathUtils.deleteDirectory(outputDirPath);
        }
        Files.createDirectory(outputDirPath);

        // 生成器
        AutoGenerator generator = getGenerator(dataSourceConfig, moduleName, "org.xiangqian.monolithic.common.mysql", author, tables, outputDirPath);

        // 模板引擎
        AbstractTemplateEngine templateEngine = new VelocityTemplateEngine() {
            @Override
            public String templateFilePath(String filePath) {
                return "/mybatis" + super.templateFilePath(filePath);
            }
        };

        // 执行
        generator.execute(templateEngine);

        // 打印输出目录
        System.out.format("输出目录 %s\n", outputDirPath).println();
    }

    /**
     * 获取生成器
     *
     * @param dataSourceConfig 数据源配置
     * @param moduleName       模块名
     * @param basePkg          基础包
     * @param author           作者
     * @param tables           数据表集合
     * @param outputDirPath    输出目录
     * @return
     */
    private static AutoGenerator getGenerator(DataSourceConfig dataSourceConfig, String moduleName, String basePkg, String author, String[] tables, Path outputDirPath) {
        // 生成器
        AutoGenerator generator = new AutoGenerator(dataSourceConfig);

        // 全局配置
        generator.global(new GlobalConfig.Builder()
                .author(author)
                .enableSwagger()
                .disableOpenDir()
                .outputDir(outputDirPath.resolve("java").toAbsolutePath().toString())
                .dateType(DateType.TIME_PACK)
                .commentDate("HH:mm yyyy/MM/dd")
                .build());

        // 包配置
        generator.packageInfo(new PackageConfig.Builder()
                .parent(basePkg)
                .moduleName(moduleName)
                .entity("entity")
                .mapper("mapper")
                .service("service")
                .serviceImpl("service.impl")
                .controller("controller")
                .pathInfo(Map.of(OutputFile.xml, outputDirPath.resolve("resources").resolve("mybatis").resolve("mapper").resolve(moduleName).toAbsolutePath().toString()))
                .build());

        // 策略配置
        generator.strategy(new StrategyConfig.Builder()
                // 数据表前缀
                .addTablePrefix(moduleName)
                // 包含的数据表集
                .addInclude(tables)

                // entity构建
                .entityBuilder()
//                .superClass(null)
                .enableLombok()
                .logicDeleteColumnName("del")
                .logicDeletePropertyName("del")
                .formatFileName("%sEntity")
                .enableFileOverride()

                // mapper构建
                .mapperBuilder()
                .enableMapperAnnotation()
                .enableBaseResultMap()
                .enableBaseColumnList()
                .enableFileOverride()

                // service构建
                .serviceBuilder()
                .formatServiceFileName("%sService")
                .formatServiceImplFileName("%sServiceImpl")
                .enableFileOverride()

                // controller构建
                .controllerBuilder()
                .enableRestStyle()
                .enableHyphenStyle()
                .enableFileOverride()
                .build());

        generator.injection(new InjectionConfig.Builder().build());
        generator.template(new TemplateConfig.Builder()
                // 设置不生成 service
                .service("")
                .serviceImpl("")
                // 设置不生成 controller
                .controller("")
                .build());
        return generator;
    }

}
