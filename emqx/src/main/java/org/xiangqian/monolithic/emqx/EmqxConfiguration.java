package org.xiangqian.monolithic.emqx;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.xiangqian.monolithic.common.emqx.Emqx;
import org.xiangqian.monolithic.common.emqx.EmqxProperties;

/**
 * @author xiangqian
 * @date 21:11 2024/06/28
 */
@Configuration(proxyBeanMethods = false)
public class EmqxConfiguration {

    @Bean
    public Emqx emqx(EmqxProperties emqxProperties) {
        return new DefaultEmqx(emqxProperties);
    }

}
