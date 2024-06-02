package org.xiangqian.monolithic.web.generator;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import org.apache.commons.io.file.PathUtils;
import org.apache.commons.lang3.StringUtils;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MyBatis v3.5.3.1 代码生成器
 *
 * @author xiangqian
 * @date 19:52 2023/05/19
 */
public class MyBatisGenerator {

    public static void main(String[] args) throws IOException {
        execute("sys",
                "xiangqian",
                new String[]{"role"},
                false);
    }

    /**
     * 执行
     *
     * @param moduleName      模块名
     * @param author          作者
     * @param tables          数据表集合
     * @param replaceExisting 如果目标文件已存在则覆盖
     */
    public static void execute(String moduleName,
                               String author,
                               String[] tables,
                               boolean replaceExisting) throws IOException {
        // 输出目录
        Path outputDir = getOutputDir();

        // 获取生成器
        AutoGenerator generator = getGenerator(moduleName, "org.xiangqian.monolithic", author, tables, outputDir);

        // 获取模板引擎
        AbstractTemplateEngine templateEngine = getTemplateEngine();

        // 执行
        generator.execute(templateEngine);

        // 打印输出目录
        System.out.format("输出目录 %s\n", outputDir).println();
        Path javaPath = outputDir.resolve("java");
        Path resourcesPath = outputDir.resolve("resources");

        // web
        Path webPath = outputDir.resolve("web");
        if (Files.exists(webPath)) {
            PathUtils.deleteDirectory(webPath);
        }
        Files.createDirectory(webPath);
        PathUtils.copyDirectory(javaPath, webPath.resolve("java"));
        PathUtils.deleteDirectory(webPath.resolve("java").resolve("org").resolve("xiangqian").resolve("monolithic").resolve("biz"));

        // /monolithic/web/target/test-classes
        Path testClassesPath = Paths.get(MyBatisGenerator.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1));
        // /monolithic/web/target
        Path targetPath = testClassesPath.getParent();
        // /monolithic/web
        Path projectPath = targetPath.getParent();
        // /monolithic/web/src/main
        Path mainPath = projectPath.resolve("src").resolve("main");
        copyDirectory(webPath, mainPath, replaceExisting, srcPath -> {
            String fileName = srcPath.getFileName().toString();
            if (fileName.endsWith("Controller.java")) {
                try {
                    String regex = "\"([^\"]*)\"";
                    Pattern pattern = Pattern.compile(regex);

                    boolean modified = false;
                    List<String> lines = Files.readAllLines(srcPath);
                    for (int i = 0, size = lines.size(); i < size; i++) {
                        String line = lines.get(i);
                        if (line.startsWith("@Tag(name = ")) {
                            Matcher matcher = pattern.matcher(line);
                            StringBuffer stringBuffer = new StringBuffer();
                            while (matcher.find()) {
                                String string = matcher.group(1);
                                if (string.endsWith("表")) {
                                    string = string.substring(0, string.length() - 1);
                                }
                                string += "接口";
                                matcher.appendReplacement(stringBuffer, String.format("\"%s\"", string));
                            }
                            matcher.appendTail(stringBuffer);
                            lines.set(i, stringBuffer.toString());
                            modified = true;

                        } else if (line.startsWith("@RequestMapping(")) {
                            Matcher matcher = pattern.matcher(line);
                            StringBuffer stringBuffer = new StringBuffer();
                            while (matcher.find()) {
                                String string = matcher.group(1);
                                if (string.endsWith("-entity")) {
                                    string = string.substring(0, string.length() - "-entity".length());
                                }
                                matcher.appendReplacement(stringBuffer, String.format("\"%s\"", "/api/" + moduleName + string));
                            }
                            matcher.appendTail(stringBuffer);
                            lines.set(i, stringBuffer.toString());
                            modified = true;
                        }
                    }

                    if (modified) {
                        Files.writeString(srcPath, StringUtils.join(lines, "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        // biz
        Path bizPath = outputDir.resolve("biz");
        if (Files.exists(bizPath)) {
            PathUtils.deleteDirectory(bizPath);
        }
        Files.createDirectory(bizPath);
        PathUtils.copyDirectory(javaPath, bizPath.resolve("java"));
        PathUtils.copyDirectory(resourcesPath, bizPath.resolve("resources"));
        PathUtils.deleteDirectory(bizPath.resolve("java").resolve("org").resolve("xiangqian").resolve("monolithic").resolve("web"));

        // /monolithic/biz
        projectPath = projectPath.getParent().resolve("biz");
        // /monolithic/biz/src/main
        mainPath = projectPath.resolve("src").resolve("main");
        copyDirectory(bizPath, mainPath, replaceExisting, srcPath -> {
            String fileName = srcPath.getFileName().toString();
            if (fileName.endsWith("Entity.java")) {
                try {
                    String regex = "\"([^\"]*)\"";
                    Pattern pattern = Pattern.compile(regex);

                    boolean modified = false;
                    List<String> lines = Files.readAllLines(srcPath);
                    for (int i = 0, size = lines.size(); i < size; i++) {
                        String line = lines.get(i);
                        if (line.startsWith("@Schema(description = ")) {
                            Matcher matcher = pattern.matcher(line);
                            StringBuffer stringBuffer = new StringBuffer();
                            while (matcher.find()) {
                                String string = matcher.group(1);
                                if (string.endsWith("表")) {
                                    string = string.substring(0, string.length() - 1);
                                }
                                if (!string.endsWith("string")) {
                                    string += "信息";
                                }
                                matcher.appendReplacement(stringBuffer, String.format("\"%s\"", string));
                            }
                            matcher.appendTail(stringBuffer);
                            lines.set(i, stringBuffer.toString());
                            modified = true;
                        }
                    }
                    if (modified) {
                        Files.writeString(srcPath, StringUtils.join(lines, "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (fileName.endsWith("Mapper.java")) {
                try {
                    boolean modified = false;
                    List<String> lines = Files.readAllLines(srcPath);
                    for (int i = 0, size = lines.size(); i < size; i++) {
                        String line = lines.get(i);
                        if (line.startsWith(" * ") && line.endsWith("表Mapper")) {
                            line = line.substring(0, line.length() - "表Mapper".length()) + "Mapper";
                            lines.set(i, line);
                            modified = true;
                        }
                    }
                    if (modified) {
                        Files.writeString(srcPath, StringUtils.join(lines, "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (fileName.endsWith("Service.java")) {
                try {
                    boolean modified = false;
                    List<String> lines = Files.readAllLines(srcPath);
                    for (int i = 0, size = lines.size(); i < size; i++) {
                        String line = lines.get(i);
                        if (line.startsWith(" * ") && line.endsWith("表服务")) {
                            line = line.substring(0, line.length() - "表服务".length()) + "服务";
                            lines.set(i, line);
                            modified = true;
                        }
                    }
                    if (modified) {
                        Files.writeString(srcPath, StringUtils.join(lines, "\n"));
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

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
    private static void copyDirectory(Path srcDirPath, Path dstDirPath, boolean replaceExisting, Consumer<Path> srcPathConsumer) throws IOException {
        Files.walk(srcDirPath).forEach(srcPath -> {
            Path dstPath = dstDirPath.resolve(srcDirPath.relativize(srcPath));
            try {
                if (srcPathConsumer != null && !Files.isDirectory(srcPath)) {
                    srcPathConsumer.accept(srcPath);
                }

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
     * @param moduleName 模块名
     * @param basePkg    基础包
     * @param author     作者
     * @param tables     数据表集合
     * @param outputDir  输出目录
     * @return
     */
    private static AutoGenerator getGenerator(String moduleName, String basePkg, String author, String[] tables, Path outputDir) throws IOException {
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
                .entity(String.format("biz.%s.entity", moduleName))
                .mapper(String.format("biz.%s.mapper", moduleName))
                .service(String.format("biz.%s.service", moduleName))
                .serviceImpl(String.format("biz.%s.service.impl", moduleName))
                .controller(String.format("web.%s.controller", moduleName))
                .pathInfo(Map.of(OutputFile.xml, outputDir.resolve("resources").resolve("mybatis").resolve("mapper").resolve(moduleName).toFile().getAbsolutePath()))
                .build());

        // 策略配置
        generator.strategy(new StrategyConfig.Builder()
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
     * 获取数据源配置
     *
     * @return
     */
    private static DataSourceConfig getDataSourceConfig() throws IOException {
        // /monolithic/web/target/test-classes
        Path testClassesPath = Paths.get(MyBatisGenerator.class.getProtectionDomain().getCodeSource().getLocation().getPath().substring(1));
        // /monolithic/web/target
        Path targetPath = testClassesPath.getParent();
        // /monolithic/web
        Path projectPath = targetPath.getParent();
        // /monolithic/web/src/main/resources/application-dev.yml
        Path path = projectPath.resolve("src").resolve("main").resolve("resources").resolve("application-dev.yml");

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
