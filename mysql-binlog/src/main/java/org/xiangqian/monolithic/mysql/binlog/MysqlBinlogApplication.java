package org.xiangqian.monolithic.mysql.binlog;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author xiangqian
 * @date 22:33 2024/06/27
 */
@SpringBootApplication
public class MysqlBinlogApplication implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(MysqlBinlogApplication.class, args);
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    @ConfigurationProperties(prefix = "mysqls")
    public List<MysqlProperties> mysqlPropertiesList() {
        return new ArrayList<>(2);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<MysqlProperties> mysqlPropertiesList = (List<MysqlProperties>) applicationContext.getBean("mysqlPropertiesList");
        for (MysqlProperties mysqlProperties : mysqlPropertiesList) {
            MysqlBinlogClient mysqlBinlogClient = new MysqlBinlogClient(applicationContext, mysqlProperties);
            new Thread(mysqlBinlogClient, String.format("Thread-MySQL-BinLog-%s", mysqlProperties.getName())).start();
            Runtime.getRuntime().addShutdownHook(new Thread(() -> IOUtils.closeQuietly(mysqlBinlogClient)));
        }
    }

}