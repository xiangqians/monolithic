package org.xiangqian.monolithic.web.generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import lombok.SneakyThrows;
import org.apache.commons.io.file.PathUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.Map;

/**
 * MyBatis v3.5.3.1 代码生成器
 *
 * @author xiangqian
 * @date 19:52 2023/05/19
 */
public class MyBatisGenerator {

    // 环境变量：
    // DB_DRIVER_CLASS_NAME=com.mysql.cj.jdbc.Driver
    // DB_URL=jdbc:mysql://localhost:3306/monolithic?serverTimezone=Asia/Shanghai&useUnicode=true&characterEncoding=utf-8&zeroDateTimeBehavior=convertToNull&useSSL=false&allowPublicKeyRetrieval=true
    // DB_USERNAME=root
    // DB_PASSWORD=root
    @SneakyThrows
    public static void main(String[] args) {
        String driverClassName = System.getenv("DB_DRIVER_CLASS_NAME");
        String url = System.getenv("DB_URL");
        String username = System.getenv("DB_USERNAME");
        String password = System.getenv("DB_PASSWORD");
        System.out.format("数据源\n\tdriverClassName : %s" +
                        "\n\turl\t\t\t\t: %s" +
                        "\n\tusername\t\t: %s" +
                        "\n\tpassword\t\t: %s",
                driverClassName, url, username, password).println();
        execute(driverClassName, url, username, password,
                "sched",
                "xiangqian",
                new String[]{"sched_task", "sched_task_record"},
                false);
    }

    /**
     * 执行
     *
     * @param driverClassName 数据库驱动
     * @param url             数据库url
     * @param username        数据库用户名
     * @param password        数据库密码
     * @param moduleName      模块名
     * @param author          作者
     * @param tables          数据表集合
     * @param replaceExisting 如果目标文件已存在则覆盖
     * @throws IOException
     */
    public static void execute(String driverClassName, String url, String username, String password, String moduleName, String author, String[] tables, boolean replaceExisting) throws ClassNotFoundException, IOException {
        // 输出目录
        Path outputDirPath = getOutputDirPath();

        // 数据源配置
        Class.forName(driverClassName);
        DataSourceConfig dataSourceConfig = new DataSourceConfig.Builder(url, username, password)
                .dbQuery(new MySqlQuery())
                .typeConvert(new MySqlTypeConvert())
                .keyWordsHandler(new MySqlKeyWordsHandler())
                .build();

        // 获取生成器
        AutoGenerator generator = getGenerator(dataSourceConfig, moduleName, "org.xiangqian.monolithic.common.db", author, tables, outputDirPath);

        // 获取模板引擎
        AbstractTemplateEngine templateEngine = getTemplateEngine();

        // 执行
        generator.execute(templateEngine);

        // 打印输出目录
        System.out.format("输出目录 %s\n", outputDirPath).println();

        // 生成的java
        Path javaPath = outputDirPath.resolve("java");
        Path modulePath = javaPath.resolve("org").resolve("xiangqian").resolve("monolithic").resolve("common").resolve("db").resolve(moduleName);
        // 删除生成的service
        PathUtils.deleteDirectory(modulePath.resolve("service"));
        // 删除生成的controller
        PathUtils.deleteDirectory(modulePath.resolve("controller"));

        // 生成的resources
        Path resourcesPath = outputDirPath.resolve("resources");

        // 将要拷贝到的目标路径
        // monolithic/common/common-mysql/target
        Path targetPath = outputDirPath.getParent();
        // monolithic/common/common-mysql
        Path projectPath = targetPath.getParent();
        // monolithic/common/common-mysql/src/main
        Path mainPath = projectPath.resolve("src").resolve("main");

        // 拷贝目录
        copyDirectory(javaPath, mainPath.resolve("java"), replaceExisting);
        copyDirectory(resourcesPath, mainPath.resolve("resources"), replaceExisting);

        // 删除 monolithic/common/common-mysql/target/generated-test-sources/java、monolithic/common/common-mysql/target/generated-test-sources/resources 目录
        PathUtils.deleteDirectory(javaPath);
        PathUtils.deleteDirectory(resourcesPath);
    }

    /**
     * 拷贝源目录到目标目录
     *
     * @param srcDirPath
     * @param dstDirPath
     * @param replaceExisting
     * @throws IOException
     */
    private static void copyDirectory(Path srcDirPath, Path dstDirPath, boolean replaceExisting) throws IOException {
        Files.walk(srcDirPath).forEach(srcPath -> {
            Path dstPath = dstDirPath.resolve(srcDirPath.relativize(srcPath));
            try {
                if (replaceExisting) {
                    // 如果目标文件已存在则覆盖
                    Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING);
                } else {
                    // 如果目标文件已存在则会抛出异常
                    Files.copy(srcPath, dstPath);
                }
            } catch (IOException e) {
                if (e instanceof DirectoryNotEmptyException) {
                    return;
                }

                if (e instanceof FileAlreadyExistsException) {
                    if (!Files.isDirectory(dstPath)) {
                        System.err.format("%s 文件已存在，忽略拷贝", dstPath).println();
                    }
                    return;
                }

                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 获取输出目录
     *
     * @return
     */
    private static Path getOutputDirPath() {
        URL url = Thread.currentThread().getContextClassLoader().getResource("");
        String path = new File(url.getPath().substring(1)).getParentFile().getPath();
        File file = Paths.get(path, "generated-test-sources").toFile();
        if (!file.exists()) {
            file.mkdirs();
        }
        return file.toPath();
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
    private static AutoGenerator getGenerator(DataSourceConfig dataSourceConfig, String moduleName, String basePkg, String author, String[] tables, Path outputDirPath) throws IOException {
        // 生成器
        AutoGenerator generator = new AutoGenerator(dataSourceConfig);

        // 全局配置
        generator.global(new GlobalConfig.Builder()
                .author(author)
                .enableSwagger()
                .disableOpenDir()
                .outputDir(outputDirPath.resolve("java").toFile().getAbsolutePath())
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
                .pathInfo(Map.of(OutputFile.xml, outputDirPath.resolve("resources").resolve("mybatis").resolve("mapper").resolve(moduleName).toFile().getAbsolutePath()))
                .build());

        // 策略配置
        generator.strategy(new StrategyConfig.Builder()
                .addTablePrefix(moduleName)
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
        generator.template(new TemplateConfig.Builder().build());
        return generator;
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
