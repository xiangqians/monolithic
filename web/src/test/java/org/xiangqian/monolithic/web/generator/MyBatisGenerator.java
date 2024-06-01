package org.xiangqian.monolithic.web.generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
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

    public static void main(String[] args) throws IOException {
        execute("xiangqian", "role");
    }

    /**
     * 执行
     *
     * @param author 作者
     * @param tables 数据表集合
     */
    public static void execute(String author, String... tables) throws IOException {
        // 基础包
        String basePkg = "org.xiangqian.monolithic";

        // 输出目录
        Path outputDir = getOutputDir();

        // 获取生成器
        AutoGenerator generator = getGenerator(basePkg, author, tables, outputDir);

        // 获取模板引擎
        AbstractTemplateEngine templateEngine = getTemplateEngine();

        // 执行
        generator.execute(templateEngine);

        // 打印输出目录
        System.out.format("输出目录 %s", outputDir).println();
    }

    /**
     * 获取输出目录
     *
     * @return
     */
    private static Path getOutputDir() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String path = new File(url.getPath().substring(1)).getParentFile().getPath();
        File file = Paths.get(path, "generated-sources").toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.toPath();
    }

    /**
     * 获取生成器
     *
     * @param basePkg   基础包
     * @param author    作者
     * @param tables    数据表集合
     * @param outputDir 输出目录
     * @return
     */
    private static AutoGenerator getGenerator(String basePkg, String author, String[] tables, Path outputDir) throws IOException {
        // 获取数据源配置
        DataSourceConfig dataSourceConfig = getDataSourceConfig();

        // 生成器
        AutoGenerator generator = new AutoGenerator(dataSourceConfig);

        // 全局配置
        generator.global(new GlobalConfig.Builder()
                .author(author)
                .enableSwagger()
                .disableOpenDir()
                .outputDir(outputDir.resolve("java").toFile().getAbsolutePath())
                .dateType(DateType.TIME_PACK)
                .commentDate("HH:mm yyyy/MM/dd")
                .build());

        // 包配置
        generator.packageInfo(new PackageConfig.Builder()
                .parent(basePkg)
                .moduleName(null)
                .entity("db.entity")
                .mapper("db.mapper")
                .service("biz.service")
                .serviceImpl("biz.service.impl")
                .controller("webmvc.controller")
                .pathInfo(Map.of(OutputFile.xml, outputDir.resolve("resources").resolve("mybatis").resolve("mapper").toFile().getAbsolutePath()))
                .build());

        // 策略配置
        generator.strategy(new StrategyConfig.Builder()
                .addInclude(tables)

                // entity构建
                .entityBuilder()
//                .superClass(Entity.class)
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
        generator.template(new TemplateConfig.Builder().build());
        return generator;
    }

    /**
     * 获取数据源配置
     *
     * @return
     */
    private static DataSourceConfig getDataSourceConfig() throws IOException {
        // \monolithic\webmvc\target\test-classes
        Path testClasses = Paths.get(MyBatisGenerator.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1));
        // \monolithic\webmvc\target
        Path target = testClasses.getParent();
        // \monolithic\webmvc
        Path project = target.getParent();
        // \monolithic\webmvc\src\main\resources\application-dev.yml
        Path path = project.resolve("src").resolve("main").resolve("resources").resolve("application-dev.yml");

        Yaml yaml = new Yaml();
        Map map = yaml.loadAs(Files.readString(path), Map.class);
        Map spring = (Map) map.get("spring");
        Map datasource = (Map) spring.get("datasource");
        String driverClassName = (String) datasource.get("driver-class-name");
        String url = (String) datasource.get("url");
        String username = (String) datasource.get("username");
        String password = (String) datasource.get("password");

        System.out.format("数据源\n\tdriverClassName : %s" +
                        "\n\turl\t\t\t\t: %s" +
                        "\n\tusername\t\t: %s" +
                        "\n\tpassword\t\t: %s",
                driverClassName, url, username, "******").println();

        return new DataSourceConfig.Builder(url, username, password)
                .dbQuery(new MySqlQuery())
                .typeConvert(new MySqlTypeConvert())
                .keyWordsHandler(new MySqlKeyWordsHandler())
                .build();
    }

    /**
     * 获取模板引擎
     *
     * @return
     */
    private static AbstractTemplateEngine getTemplateEngine() {
        return new VelocityTemplateEngine() {
            @Override
            public String templateFilePath(String filePath) {
                return "/mybatis" + super.templateFilePath(filePath);
            }
        };
    }

}
