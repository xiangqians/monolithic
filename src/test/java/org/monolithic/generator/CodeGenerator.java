package org.monolithic.generator;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.IDatabaseQuery;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.builder.ConfigBuilder;
import com.baomidou.mybatisplus.generator.config.builder.Entity;
import com.baomidou.mybatisplus.generator.config.converts.MySqlTypeConvert;
import com.baomidou.mybatisplus.generator.config.po.TableField;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.querys.DbQueryDecorator;
import com.baomidou.mybatisplus.generator.config.querys.MySqlQuery;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.IColumnType;
import com.baomidou.mybatisplus.generator.engine.AbstractTemplateEngine;
import com.baomidou.mybatisplus.generator.engine.VelocityTemplateEngine;
import com.baomidou.mybatisplus.generator.jdbc.DatabaseMetaDataWrapper;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.monolithic.o.Po;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * mybatis（v3.5.2）代码生成器
 *
 * @author xiangqian
 * @date 21:47 2022/08/15
 */
@Data
@Slf4j
public class CodeGenerator {

    private String author;

    private String driverClassName;
    private String url;
    private String username;
    private String password;

    // 模块名
    private String moduleName;

    // 数据表名
    private String[] tables;

    private String outputDir;

    private CodeGenerator() {
    }

    public static class Builder {
        protected CodeGenerator codeGenerator;
        private DBConfig dbConfig;
        private OutputConfig outputConfig;

        public Builder() {
            codeGenerator = new CodeGenerator();
        }

        public OutputConfig outputConfig() {
            if (outputConfig == null) {
                outputConfig = new OutputConfig(this);
            }
            return outputConfig;
        }

        public DBConfig dbConfig() {
            if (dbConfig == null) {
                dbConfig = new DBConfig(this);
            }
            return dbConfig;
        }

        public Builder author(String author) {
            codeGenerator.setAuthor(author);
            return this;
        }


        public CodeGenerator build() {
            return codeGenerator;
        }
    }

    public static class OutputConfig {
        private Builder builder;

        private OutputConfig(Builder builder) {
            this.builder = builder;
        }

        public OutputConfig tables(String... tables) {
            builder.codeGenerator.setTables(tables);
            return this;
        }

        public OutputConfig moduleName(String moduleName) {
            builder.codeGenerator.setModuleName(moduleName);
            return this;
        }

        public OutputConfig outputDir(String outputDir) {
            builder.codeGenerator.setOutputDir(outputDir);
            return this;
        }

        public Builder and() {
            if (StringUtils.isEmpty(builder.codeGenerator.getOutputDir())) {
                builder.codeGenerator.setOutputDir(getDefaultOutputDir());
            }
            return builder;
        }

        /**
         * 获取默认输出路径
         *
         * @return
         */
        private String getDefaultOutputDir() {
            URL url = this.getClass().getClassLoader().getResource("");
            String targetPath = new File(url.getPath().substring(1)).getParentFile().getPath();
            File file = new File(String.format("%s%s%s%s%s",
                    targetPath,
                    File.separator,
                    "generated-sources",
                    File.separator,
                    "mybatis-generator"));
            if (!file.exists()) {
                file.mkdirs();
            }
            return file.getPath();
        }
    }

    public static class DBConfig {
        private Builder builder;

        private DBConfig(Builder builder) {
            this.builder = builder;
        }

        public DBConfig driverClassName(String driverClassName) {
            builder.codeGenerator.setDriverClassName(driverClassName);
            return this;
        }

        public DBConfig url(String url) {
            builder.codeGenerator.setUrl(url);
            return this;
        }

        public DBConfig username(String username) {
            builder.codeGenerator.setUsername(username);
            return this;
        }

        public DBConfig password(String password) {
            builder.codeGenerator.setPassword(password);
            return this;
        }

        public Builder and() {
            return builder;
        }
    }

    public void execute() {
        // 数据源配置
        // mysql
        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig.Builder(url, username, password)
                .dbQuery(new MySqlQuery())
                .typeConvert(new MySqlTypeConvert())
                .keyWordsHandler(new MySqlKeyWordsHandler());
        // sqlite
//        DataSourceConfig.Builder dataSourceConfigBuilder = new DataSourceConfig.Builder(url, username, password)
//                .dbQuery(new SqliteQuery())
//                .typeConvert(new SqliteTypeConvert());

        FastAutoGenerator fastAutoGenerator = FastAutoGenerator.create(dataSourceConfigBuilder);

        // 全局配置
        fastAutoGenerator.globalConfig(builder -> builder
                .author(author)
                .fileOverride()
                .disableOpenDir()
                .outputDir(String.format("%s%sjava", outputDir, File.separator))
                .dateType(DateType.TIME_PACK)
                .commentDate("HH:mm yyyy/MM/dd"));

        // 包配置
        fastAutoGenerator.packageConfig(builder -> builder
                .parent("org.monolithic")
                .moduleName(moduleName)
                .entity("po")
                .service("service")
                .serviceImpl("service.impl")
                .mapper("mapper")
                .controller("controller")
                .other("other")
                .pathInfo(Map.of(OutputFile.xml, outputDir + String.format("%sresources%smybatis%smapper", File.separator, File.separator, File.separator)))
        );

        // 策略配置
        fastAutoGenerator.strategyConfig(builder -> builder
                .addInclude(tables)

                .entityBuilder()
                .superClass(Po.class)
                .enableLombok()
                .logicDeleteColumnName("del_flag")
                .logicDeletePropertyName("delFlag")
                .formatFileName("%sPo")

                .mapperBuilder()
                .enableMapperAnnotation()
                .enableBaseResultMap()
                .enableBaseColumnList()

                .serviceBuilder()
                .formatServiceFileName("%sService")
                .formatServiceImplFileName("%sServiceImpl")

                .controllerBuilder()
                .enableRestStyle()
                .enableHyphenStyle()

                .build()
        );

        // 配置模板引擎
        fastAutoGenerator.templateEngine(new VelocityTemplateEngine() {
            @Override
            public @NotNull
            String templateFilePath(@NotNull String filePath) {
                return "/custom" + super.templateFilePath(filePath);
            }
        });

        // 执行
        fastAutoGenerator.execute();
        log.debug("OutputDir: {}", outputDir);
    }


    /**
     * 处理以下划线开头的字段（'_xxx'）
     */
    public static class FastAutoGenerator {
        private final DataSourceConfig.Builder dataSourceConfigBuilder;
        private final GlobalConfig.Builder globalConfigBuilder;
        private final PackageConfig.Builder packageConfigBuilder;
        private final StrategyConfig.Builder strategyConfigBuilder;
        private final InjectionConfig.Builder injectionConfigBuilder;
        private final TemplateConfig.Builder templateConfigBuilder;
        private AbstractTemplateEngine templateEngine;
        private final Scanner scanner;

        private FastAutoGenerator(DataSourceConfig.Builder dataSourceConfigBuilder) {
            this.scanner = new Scanner(System.in);
            this.dataSourceConfigBuilder = dataSourceConfigBuilder;
            this.globalConfigBuilder = new GlobalConfig.Builder();
            this.packageConfigBuilder = new PackageConfig.Builder();
            this.strategyConfigBuilder = new StrategyConfig.Builder();
            this.injectionConfigBuilder = new InjectionConfig.Builder();
            this.templateConfigBuilder = new TemplateConfig.Builder();
        }

        public static FastAutoGenerator create(@NotNull String url, String username, String password) {
            return new FastAutoGenerator(new DataSourceConfig.Builder(url, username, password));
        }

        public static FastAutoGenerator create(@NotNull DataSourceConfig.Builder dataSourceConfigBuilder) {
            return new FastAutoGenerator(dataSourceConfigBuilder);
        }

        public String scannerNext(String message) {
            System.out.println(message);
            String nextLine = scanner.nextLine();
            return com.baomidou.mybatisplus.core.toolkit.StringUtils.isBlank(nextLine) ? scanner.next() : nextLine;
        }

        public FastAutoGenerator globalConfig(Consumer<GlobalConfig.Builder> consumer) {
            consumer.accept(globalConfigBuilder);
            return this;
        }

        public FastAutoGenerator globalConfig(BiConsumer<Function<String, String>, GlobalConfig.Builder> biConsumer) {
            biConsumer.accept((message) -> scannerNext(message), globalConfigBuilder);
            return this;
        }

        public FastAutoGenerator packageConfig(Consumer<PackageConfig.Builder> consumer) {
            consumer.accept(packageConfigBuilder);
            return this;
        }

        public FastAutoGenerator packageConfig(BiConsumer<Function<String, String>, PackageConfig.Builder> biConsumer) {
            biConsumer.accept((message) -> scannerNext(message), packageConfigBuilder);
            return this;
        }

        public FastAutoGenerator strategyConfig(Consumer<StrategyConfig.Builder> consumer) {
            consumer.accept(strategyConfigBuilder);
            return this;
        }

        public FastAutoGenerator strategyConfig(BiConsumer<Function<String, String>, StrategyConfig.Builder> biConsumer) {
            biConsumer.accept((message) -> scannerNext(message), strategyConfigBuilder);
            return this;
        }

        public FastAutoGenerator injectionConfig(Consumer<InjectionConfig.Builder> consumer) {
            consumer.accept(injectionConfigBuilder);
            return this;
        }

        public FastAutoGenerator injectionConfig(BiConsumer<Function<String, String>, InjectionConfig.Builder> biConsumer) {
            biConsumer.accept((message) -> scannerNext(message), injectionConfigBuilder);
            return this;
        }

        public FastAutoGenerator templateConfig(Consumer<TemplateConfig.Builder> consumer) {
            consumer.accept(templateConfigBuilder);
            return this;
        }

        public FastAutoGenerator templateConfig(BiConsumer<Function<String, String>, TemplateConfig.Builder> biConsumer) {
            biConsumer.accept((message) -> scannerNext(message), templateConfigBuilder);
            return this;
        }

        public FastAutoGenerator templateEngine(AbstractTemplateEngine templateEngine) {
            this.templateEngine = templateEngine;
            return this;
        }

        public void execute() {
            AutoGenerator autoGenerator = new AutoGenerator(dataSourceConfigBuilder.build());
            autoGenerator.global(globalConfigBuilder.build());
            autoGenerator.packageInfo(packageConfigBuilder.build());
            autoGenerator.strategy(strategyConfigBuilder.build());
            autoGenerator.injection(injectionConfigBuilder.build());
            autoGenerator.template(templateConfigBuilder.build());
            autoGenerator.config(new CustomConfigBuilder(autoGenerator.getPackageInfo(),
                    autoGenerator.getDataSource(),
                    autoGenerator.getStrategy(),
                    autoGenerator.getTemplate(),
                    autoGenerator.getGlobalConfig(),
                    autoGenerator.getInjectionConfig()));
            autoGenerator.execute(templateEngine);
        }
    }

    public static class CustomConfigBuilder extends ConfigBuilder {

        private static final Field TABLE_INFO_LIST_FIELD;

        static {
            try {
                TABLE_INFO_LIST_FIELD = ConfigBuilder.class.getDeclaredField("tableInfoList");
                TABLE_INFO_LIST_FIELD.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new Error(e);
            }
        }

        public CustomConfigBuilder(@Nullable PackageConfig packageConfig,
                                   @NotNull DataSourceConfig dataSourceConfig,
                                   @Nullable StrategyConfig strategyConfig,
                                   @Nullable TemplateConfig templateConfig,
                                   @Nullable GlobalConfig globalConfig,
                                   @Nullable InjectionConfig injectionConfig) {
            super(packageConfig, dataSourceConfig, strategyConfig, templateConfig, globalConfig, injectionConfig);
        }

        @Override
        public @NotNull
        List<TableInfo> getTableInfoList() {
            List<TableInfo> tableInfoList = null;
            try {
                tableInfoList = (List<TableInfo>) TABLE_INFO_LIST_FIELD.get(this);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (tableInfoList.isEmpty()) {
                List<TableInfo> tableInfos = (new DefaultDatabaseQuery(this)).queryTables();
                if (!tableInfos.isEmpty()) {
                    tableInfoList.addAll(tableInfos);
                }
            }

            return tableInfoList;
        }

    }

    public static class DefaultDatabaseQuery extends IDatabaseQuery {
        private static final Logger LOGGER = LoggerFactory.getLogger(IDatabaseQuery.DefaultDatabaseQuery.class);
        private final StrategyConfig strategyConfig;
        private final GlobalConfig globalConfig;
        private final DbQueryDecorator dbQuery;

        public DefaultDatabaseQuery(@NotNull ConfigBuilder configBuilder) {
            super(configBuilder);
            this.strategyConfig = configBuilder.getStrategyConfig();
            this.dbQuery = new DbQueryDecorator(this.dataSourceConfig, this.strategyConfig);
            this.globalConfig = configBuilder.getGlobalConfig();
        }

        @NotNull
        public List<TableInfo> queryTables() {
            boolean isInclude = strategyConfig.getInclude().size() > 0;
            boolean isExclude = strategyConfig.getExclude().size() > 0;
            List<TableInfo> tableList = new ArrayList();
            List<TableInfo> includeTableList = new ArrayList();
            List<TableInfo> excludeTableList = new ArrayList();

            try {
                dbQuery.execute(dbQuery.tablesSql(), (result) -> {
                    String tableName = result.getStringResult(dbQuery.tableName());
                    if (com.baomidou.mybatisplus.core.toolkit.StringUtils.isNotBlank(tableName)) {
                        TableInfo tableInfo = new TableInfo(configBuilder, tableName);
                        String tableComment = result.getTableComment();
                        if (!strategyConfig.isSkipView() || !"VIEW".equals(tableComment)) {
                            tableInfo.setComment(tableComment);
                            if (isInclude && strategyConfig.matchIncludeTable(tableName)) {
                                includeTableList.add(tableInfo);
                            } else if (isExclude && strategyConfig.matchExcludeTable(tableName)) {
                                excludeTableList.add(tableInfo);
                            }

                            tableList.add(tableInfo);
                        }
                    }

                });
                if (isExclude || isInclude) {
                    Map<String, String> notExistTables = new HashSet<>(isExclude ? strategyConfig.getExclude() : strategyConfig.getInclude())
                            .stream()
                            .filter(s -> !ConfigBuilder.matcherRegTable(s))
                            .collect(Collectors.toMap(String::toLowerCase, (s) -> s, (o, n) -> n));
                    Iterator<TableInfo> iterator = tableList.iterator();

                    while (iterator.hasNext()) {
                        TableInfo tabInfo = iterator.next();
                        if (notExistTables.isEmpty()) {
                            break;
                        }

                        notExistTables.remove(tabInfo.getName().toLowerCase());
                    }

                    if (notExistTables.size() > 0) {
                        LOGGER.warn("表[{}]在数据库中不存在！！！", String.join(",", notExistTables.values()));
                    }

                    if (isExclude) {
                        tableList.removeAll(excludeTableList);
                    } else {
                        tableList.clear();
                        tableList.addAll(includeTableList);
                    }
                }

                tableList.forEach(this::convertTableFields);
            } catch (SQLException var12) {
                throw new RuntimeException(var12);
            } finally {
                dbQuery.closeConnection();
            }

            return tableList;
        }

        private void convertTableFields(@NotNull TableInfo tableInfo) {
            DbType dbType = dataSourceConfig.getDbType();
            String tableName = tableInfo.getName();

            try {
                Map<String, DatabaseMetaDataWrapper.ColumnsInfo> columnsMetaInfoMap = new HashMap();
                Map<String, DatabaseMetaDataWrapper.ColumnsInfo> columnsInfo = (new DatabaseMetaDataWrapper(dbQuery.getConnection())).getColumnsInfo((String) null, dataSourceConfig.getSchemaName(), tableName);
                if (columnsInfo != null && !columnsInfo.isEmpty()) {
                    columnsMetaInfoMap.putAll(columnsInfo);
                }

                String tableFieldsSql = dbQuery.tableFieldsSql(tableName);
                Set<String> h2PkColumns = new HashSet();
                if (DbType.H2 == dbType) {
                    dbQuery.execute(String.format("select * from INFORMATION_SCHEMA.INDEXES WHERE TABLE_NAME = '%s'", tableName), (result) -> {
                        String primaryKey = result.getStringResult(dbQuery.fieldKey());
                        if (Boolean.parseBoolean(primaryKey)) {
                            h2PkColumns.add(result.getStringResult(dbQuery.fieldName()));
                        }

                    });
                }

                Entity entity = strategyConfig.entity();
                dbQuery.execute(tableFieldsSql, (result) -> {
                    String columnName = result.getStringResult(dbQuery.fieldName());
                    TableField field = new CustomTableField(configBuilder, columnName);
                    boolean isId = DbType.H2 == dbType ? h2PkColumns.contains(columnName) : result.isPrimaryKey();
                    if (isId) {
                        field.primaryKey(dbQuery.isKeyIdentity(result.getResultSet()));
                        tableInfo.setHavePrimaryKey(true);
                        if (field.isKeyIdentityFlag() && entity.getIdType() != null) {
                            LOGGER.warn("当前表[{}]的主键为自增主键，会导致全局主键的ID类型设置失效!", tableName);
                        }
                    }

                    field.setColumnName(columnName).setType(result.getStringResult(dbQuery.fieldType())).setComment(result.getFiledComment()).setCustomMap(dbQuery.getCustomFields(result.getResultSet()));
                    String propertyName = entity.getNameConvert().propertyNameConvert(field);
                    IColumnType columnType = dataSourceConfig.getTypeConvert().processTypeConvert(globalConfig, field);
                    field.setPropertyName(propertyName, columnType);
                    field.setMetaInfo(new TableField.MetaInfo((DatabaseMetaDataWrapper.ColumnsInfo) columnsMetaInfoMap.get(columnName.toLowerCase())));
                    tableInfo.addField(field);
                });
            } catch (SQLException var9) {
                throw new RuntimeException(var9);
            }

            tableInfo.processTable();
        }
    }

    public static class CustomTableField extends TableField {

        private static final Field CONVERT_FIELD;

        static {
            try {
                CONVERT_FIELD = TableField.class.getDeclaredField("convert");
                CONVERT_FIELD.setAccessible(true);
            } catch (NoSuchFieldException e) {
                throw new Error(e);
            }
        }


        public CustomTableField(@NotNull ConfigBuilder configBuilder, @NotNull String name) {
            super(configBuilder, name);
        }

        @Override
        public TableField setPropertyName(@NotNull String propertyName, @NotNull IColumnType columnType) {
            TableField tableField = super.setPropertyName(propertyName, columnType);
            if (getColumnName().startsWith("_")) {
                try {
                    CONVERT_FIELD.set(this, true);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            return tableField;
        }
    }

    public static void execute(String profile, String author, String... tables) throws Exception {
        InputStream input = null;
        try {
            // user.dir
            String dir = System.getProperty("user.dir");
            log.debug("dir: {}", dir);
            Path path = Paths.get(dir, "conf", "profile", String.format("%s.properties", profile));
            log.debug("path: {}", path);
            Properties properties = new Properties();
            input = new FileInputStream(path.toFile());
            properties.load(input);
            log.debug("properties: {}", properties);

            // CodeGenerator
            CodeGenerator mybatisGenerator = new CodeGenerator.Builder()
                    .author(author)

                    // db config
                    .dbConfig()
                    .driverClassName(properties.getProperty("datasource.driver-class-name"))
                    .url(properties.getProperty("datasource.url"))
                    .username(properties.getProperty("datasource.username"))
                    .password(properties.getProperty("datasource.password"))
                    .and()

                    // output config
                    .outputConfig()
                    .moduleName(null)
                    .tables(tables)
                    .outputDir(null)
                    .and()

                    // build
                    .build();

            // execute
            mybatisGenerator.execute();
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public static void main(String[] args) throws Exception {
        execute("dev", "xiangqian",
                "oauth_client_details", "user", "role", "user_role", "perm", "role_perm", "sys_log");
    }

}
