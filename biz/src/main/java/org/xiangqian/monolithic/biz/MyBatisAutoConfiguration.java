package org.xiangqian.monolithic.biz;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * @author xiangqian
 * @date 17:07 2024/06/01
 */
@Configuration(proxyBeanMethods = false)
@MapperScan("org.xiangqian.monolithic.biz.*.mapper") // 指定要扫描的mapper包路径
@ComponentScan("org.xiangqian.monolithic.biz.*.service.impl") // 扫描组件
public class MyBatisAutoConfiguration {

    /**
     * 配置MybatisPlus分页拦截器
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        mybatisPlusInterceptor.setInterceptors(List.of(paginationInnerInterceptor));
        return mybatisPlusInterceptor;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
