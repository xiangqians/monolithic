package org.xiangqian.monolithic.common.thread.pool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xiangqian
 * @date 16:47 2024/06/22
 */
@Configuration(proxyBeanMethods = false)
public class ThreadPoolAutoConfiguration {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return DefaultThreadPoolExecutor.create();
    }

}
