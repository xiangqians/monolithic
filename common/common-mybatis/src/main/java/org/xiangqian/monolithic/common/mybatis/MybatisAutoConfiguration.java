package org.xiangqian.monolithic.common.mybatis;

import org.apache.ibatis.plugin.Interceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author xiangqian
 * @date 17:07 2024/06/01
 */
@EnableTransactionManagement
@Configuration(proxyBeanMethods = false)
public class MybatisAutoConfiguration {

    /**
     * 注册分页、延迟加载列表拦截器
     *
     * @return
     */
    @Bean
    public Interceptor mybatisInterceptor() {
        return new MybatisInterceptor();
    }

}
