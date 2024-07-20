package org.xiangqian.monolithic.common.biz;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author xiangqian
 * @date 22:56 2024/07/03
 */
@Configuration(proxyBeanMethods = false)
@ComponentScan("org.xiangqian.monolithic.common.biz.*.service.impl") // 扫描 Service 接口所在的包路径
public class BizAutoConfiguration {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}