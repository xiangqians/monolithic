package org.xiangqian.monolithic.common.mongodb;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiangqian
 * @date 20:35 2024/06/25
 */
@Configuration(proxyBeanMethods = false)
public class MongodbAutoConfiguration {

    @Bean
    public Mongodb mongodb() {
        return Mongodb.create("test");
    }

}
