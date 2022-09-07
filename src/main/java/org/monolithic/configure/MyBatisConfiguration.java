package org.monolithic.configure;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author xiangqian
 * @date 00:19 2022/08/17
 */
@Configuration
@EnableTransactionManagement
public class MyBatisConfiguration {

    /**
     * MybatisPlusInterceptor
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();

        /**
         * 分页拦截器
         * 使 {@link BaseMapper#selectPage(com.baomidou.mybatisplus.core.metadata.IPage, com.baomidou.mybatisplus.core.conditions.Wrapper)} 支持分页
         */
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        mybatisPlusInterceptor.setInterceptors(Lists.newArrayList(paginationInnerInterceptor));

        return mybatisPlusInterceptor;
    }

}
