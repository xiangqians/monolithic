package org.xiangqian.monolithic.mybatis;

import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.xiangqian.monolithic.Application;
import org.xiangqian.monolithic.util.YamlUtil;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * mybatis（v3.5.3.1）代码生成器
 *
 * @author xiangqian
 * @date 19:52 2023/05/19
 */
@Slf4j
public class MybatisGenerator {

    // 作者
    private String author;

    // 输出目录
    private String outputDir;

    // profile
    private String profile;

    // datasource
    private String datasource;

    // 所属业务名
    private String biz;

    // 数据表名
    private String[] tables;

    public MybatisGenerator(String author, String outputDir, String profile, String datasource, String biz, String[] tables) {
        this.author = author;
        this.outputDir = outputDir;
        this.profile = profile;
        this.datasource = datasource;
        this.biz = biz;
        this.tables = tables;
    }

    public static class Builder {
        private String author;
        private String outputDir;
        private String profile;
        private String datasource;
        private String biz;
        private String[] tables;

        public Builder() {
        }

        public Builder author(String author) {
            this.author = StringUtils.trimToNull(author);
            return this;
        }

        public Builder outputDir(String outputDir) {
            this.outputDir = StringUtils.trimToNull(outputDir);
            return this;
        }

        public Builder profile(String profile) {
            this.profile = StringUtils.trimToNull(profile);
            return this;
        }

        public Builder datasource(String datasource) {
            this.datasource = StringUtils.trimToNull(datasource);
            return this;
        }

        public Builder biz(String biz) {
            this.biz = StringUtils.trimToNull(biz);
            return this;
        }

        public Builder tables(String... tables) {
            if (ArrayUtils.isNotEmpty(tables)) {
                this.tables = Arrays.stream(tables)
                        .map(StringUtils::trimToNull)
                        .filter(StringUtils::isNotEmpty)
                        .toArray(String[]::new);
            }
            return this;
        }

        public MybatisGenerator build() {
            Assert.notNull(author, "author cannot be empty");

            // 默认输出路径
            if (Objects.isNull(outputDir)) {
                URL url = MybatisGenerator.class.getClassLoader().getResource("");
                String path = new File(url.getPath().substring(1)).getParentFile().getPath();
                File file = new File(String.format("%s%s%s%s%s", path, File.separator, "generated-sources", File.separator, "mybatis-generator"));
                if (!file.exists()) {
                    file.mkdirs();
                }
                outputDir = file.getPath();
            }

            Assert.notNull(profile, "profile cannot be empty");
            Assert.notNull(datasource, "datasource cannot be empty");
            Assert.notNull(biz, "biz cannot be empty");
            Assert.isTrue(ArrayUtils.isNotEmpty(tables), "tables cannot be empty");
            return new MybatisGenerator(author, outputDir, profile, datasource, biz, tables);
        }
    }

    public void execute() {
        // 获取数据源配置
        DataSourceConfig dataSourceConfig = getDataSourceConfig();

        // generator
        AutoGenerator generator = new AutoGenerator(dataSourceConfig);

        // 全局配置
        globalConfig(generator);

        // 包配置
        packageConfig(generator);

        // 策略配置
        strategyConfig(generator);

        generator.injection(new InjectionConfig.Builder().build());
        generator.template(new TemplateConfig.Builder().build());

        // 获取模板引擎
        AbstractTemplateEngine templateEngine = getTemplateEngine();

        // 执行
        generator.execute(templateEngine);

        // 打印输出目录
        log.debug("OutputDir: {}", outputDir);
    }

    /**
     * 获取模板引擎
     *
     * @return
     */
    private AbstractTemplateEngine getTemplateEngine() {
        return new VelocityTemplateEngine() {
            @Override
            public String templateFilePath(String filePath) {
                return "/mybatis" + super.templateFilePath(filePath);
            }
        };
    }

    /**
     * 策略配置
     *
     * @param generator
     */
    private void strategyConfig(AutoGenerator generator) {
        StrategyConfig.Builder builder = new StrategyConfig.Builder();
        builder
                .addInclude(tables)

                // entity构建
                .entityBuilder()
                .superClass(org.xiangqian.monolithic.model.Entity.class)
                .enableLombok()
                .logicDeleteColumnName("del")
                .logicDeletePropertyName("del")
                .formatFileName("%sEntity")

                // mapper构建
                .mapperBuilder()
                .enableMapperAnnotation()
                .enableBaseResultMap()
                .enableBaseColumnList()

                // service构建
                .serviceBuilder()
                .formatServiceFileName("%sService")
                .formatServiceImplFileName("%sServiceImpl")

                // controller构建
                .controllerBuilder()
                .enableRestStyle()
                .enableHyphenStyle();

        generator.strategy(builder.build());
    }

    /**
     * 包配置
     *
     * @param generator
     */
    private void packageConfig(AutoGenerator generator) {
        PackageConfig.Builder builder = new PackageConfig.Builder();
        builder.parent(Application.BASE_PKG)
                .moduleName(String.format("biz.%s", biz))
                .entity("entity")
                .service("service")
                .serviceImpl("service.impl")
                .mapper("mapper")
                .controller("controller")
                .pathInfo(Map.of(OutputFile.xml, outputDir + String.format("%s%s%s%s%s%s%s%s", File.separator, "resources", File.separator, "mybatis", File.separator, "mapper", File.separator, biz)));
        generator.packageInfo(builder.build());
    }

    /**
     * 全局配置
     *
     * @param generator
     */
    private void globalConfig(AutoGenerator generator) {
        GlobalConfig.Builder builder = new GlobalConfig.Builder();
        builder.author(author)
                .fileOverride()
                .disableOpenDir()
                .outputDir(String.format("%s%sjava", outputDir, File.separator))
                .dateType(DateType.TIME_PACK)
                .commentDate("HH:mm yyyy/MM/dd");
        generator.global(builder.build());
    }

    /**
     * 获取数据源配置
     *
     * @return
     */
    @SneakyThrows
    private DataSourceConfig getDataSourceConfig() {
        // {profile}.yml
        String path = System.getProperty("user.dir") + // 用户的当前工作目录
                File.separator + "profile" +
                File.separator + String.format("%s.yml", profile);
        log.debug("profile: {}", path);
        log.debug("datasource: {}", datasource);

        // load
        Map<String, Object> map = YamlUtil.load(new File(path));

        Function<String, String> getFunc = key -> {
            String value = Objects.toString(map.get(key), null);
            Assert.notNull(value, String.format("%s cannot be empty", key));
            return value;
        };

        // jdbc
        String driverClassName = getFunc.apply(String.format("datasource.%s.driver-class-name", datasource));
        String url = getFunc.apply(String.format("datasource.%s.url", datasource));
        String username = getFunc.apply(String.format("datasource.%s.username", datasource));
        String password = getFunc.apply(String.format("datasource.%s.password", datasource));

        // builder
        DataSourceConfig.Builder builder = new DataSourceConfig.Builder(url, username, password)
                .dbQuery(new MySqlQuery())
                .typeConvert(new MySqlTypeConvert())
                .keyWordsHandler(new MySqlKeyWordsHandler());

        // build
        return builder.build();
    }

}
