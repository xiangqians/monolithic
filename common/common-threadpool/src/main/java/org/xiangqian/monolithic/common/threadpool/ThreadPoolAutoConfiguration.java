package org.xiangqian.monolithic.common.threadpool;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xiangqian
 * @date 16:47 2024/06/22
 */
@Configuration(proxyBeanMethods = false)
public class ThreadpoolAutoConfiguration {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return Threadpool.create();
    }

}
