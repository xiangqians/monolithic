package org.xiangqian.monolithic.biz;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author xiangqian
 * @date 19:56 2024/05/29
 */
@Configuration(proxyBeanMethods = false)
@MapperScan("org.xiangqian.monolithic.db.*.mapper") // 指定要扫描的mapper包路径
public class DbAutoConfiguration {

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

}
