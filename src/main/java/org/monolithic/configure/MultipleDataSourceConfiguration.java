package org.monolithic.configure;

import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.transaction.TransactionManagerCustomizers;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.support.JdbcTransactionManager;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 注意！！！
 * 在使用多数据源时，声明事物需谨慎！
 *
 * @author xiangqian
 * @date 22:56 2022/09/06
 */
@Slf4j
//@Configuration
public class MultipleDataSourceConfiguration {

    private static final ThreadLocal<DataSourceName> DATA_SOURCE_NAME_THREAD_LOCAL = ThreadLocal.withInitial(() -> DataSourceName.DEFAULT);

    /**
     * 多数据源配置信息项
     *
     * @return
     */
    @Bean("dataSourcePropertiesList")
    @ConfigurationProperties(prefix = "spring.multiple-datasource")
    public List<DataSourceProperties> dataSourcePropertiesList() {
        return new ArrayList<>();
    }

    /**
     * 多数据源Map
     *
     * @param dataSourcePropertiesList
     * @return
     */
    @Bean("dataSourceMap")
    public Map<DataSourceName, DataSource> dataSourceMap(@Qualifier("dataSourcePropertiesList") List<DataSourceProperties> dataSourcePropertiesList) {
        Map<DataSourceName, DataSource> dataSourceMap = new HashMap<>();
        for (DataSourceProperties dataSourceProperties : dataSourcePropertiesList) {
            String name = dataSourceProperties.getName();
            Class<? extends DataSource> type = dataSourceProperties.getType();
            DataSource dataSource = dataSourceProperties.initializeDataSourceBuilder().type(type).build();
            if (dataSource instanceof HikariDataSource) {
                HikariDataSource hikariDataSource = (HikariDataSource) dataSource;
                hikariDataSource.setPoolName(name);
            }
            dataSourceMap.put(DataSourceName.of(name), dataSource);
        }
        return dataSourceMap;
    }

    /**
     * {@link AbstractRoutingDataSource}
     * {@link AbstractRoutingDataSource#getConnection()}
     * {@link AbstractRoutingDataSource#determineTargetDataSource()}
     * {@link AbstractRoutingDataSource#afterPropertiesSet()}
     *
     * @param dataSourceMap 多数据源映射
     * @return
     */
    @Bean
    public DataSource dataSource(@Qualifier("dataSourceMap") Map<DataSourceName, DataSource> dataSourceMap) {
        AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
            // 获取数据源key
            @Override
            protected Object determineCurrentLookupKey() {
                return Optional.ofNullable(DATA_SOURCE_NAME_THREAD_LOCAL.get()).orElse(DataSourceName.DEFAULT);
            }
        };

        // 设置默认数据源
        routingDataSource.setDefaultTargetDataSource(dataSourceMap.get(DataSourceName.DEFAULT));

        // 配置多数据源
        routingDataSource.setTargetDataSources(dataSourceMap.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));

        return routingDataSource;
    }

    /**
     * {@link org.apache.ibatis.session.defaults.DefaultSqlSession#getMapper(Class)}
     * {@link com.baomidou.mybatisplus.core.override.MybatisMapperProxy}
     * <p>
     * 在数据源事物管理中获取Connection：
     * {@link DataSourceTransactionManager#doBegin(Object, org.springframework.transaction.TransactionDefinition)}
     * {@link org.mybatis.spring.transaction.SpringManagedTransaction#openConnection()}
     * 不要多次使用 @Transactional 注解
     */
    @Aspect
    @Order(1) // 此切面要先于mapper代理接口执行
    @Component
    public static class DataSourceAspect {

        @Around("execution (* org.monolithic.other.mapper.*.*(..))")
        public Object otherAroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
            try {
                DATA_SOURCE_NAME_THREAD_LOCAL.set(DataSourceName.OTHER);
                return joinPoint.proceed();
            } finally {
                DATA_SOURCE_NAME_THREAD_LOCAL.remove();
            }
        }

    }

    /**
     * 默认数据源事物管理器
     * {@link DataSourceName#DEFAULT}
     * {@link org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration.JdbcTransactionManagerConfiguration#transactionManager(Environment, DataSource, ObjectProvider)}
     *
     * @return
     */
    @Bean
    @Primary
    public DataSourceTransactionManager defaultDataSourceTransactionManager(Environment environment,
                                                                            DataSource dataSource,
                                                                            ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        return createTransactionManager(environment, dataSource, transactionManagerCustomizers);
    }

    /**
     * other数据源事物管理器
     * {@link DataSourceName#OTHER}
     *
     * @param dataSourceMap
     * @return
     */
    @Bean
    public DataSourceTransactionManager otherDataSourceTransactionManager(Environment environment,
                                                                          @Qualifier("dataSourceMap") Map<DataSourceName, DataSource> dataSourceMap,
                                                                          ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        return createTransactionManager(environment,
                dataSourceMap.get(DataSourceName.OTHER),
                transactionManagerCustomizers);
    }

    private DataSourceTransactionManager createTransactionManager(Environment environment,
                                                                  DataSource dataSource,
                                                                  ObjectProvider<TransactionManagerCustomizers> transactionManagerCustomizers) {
        DataSourceTransactionManager transactionManager = (environment.getProperty("spring.dao.exceptiontranslation.enabled", Boolean.class, Boolean.TRUE) ? new JdbcTransactionManager(dataSource) : new DataSourceTransactionManager(dataSource));
        Optional.ofNullable(transactionManagerCustomizers).ifPresent(notNull -> notNull.ifAvailable(customizers -> customizers.customize(transactionManager)));
        return transactionManager;
    }

    /**
     * 数据源名
     */
    static enum DataSourceName {
        DEFAULT("default"),
        OTHER("other"),
        ;

        @Getter
        private final String value;

        DataSourceName(String value) {
            this.value = value;
        }

        public static DataSourceName of(String value) {
            if (value != null) {
                for (DataSourceName dataSourceName : DataSourceName.values()) {
                    if (dataSourceName.value.equals(value)) {
                        return dataSourceName;
                    }
                }
            }
            throw new UnsupportedOperationException(value);
        }
    }

}
