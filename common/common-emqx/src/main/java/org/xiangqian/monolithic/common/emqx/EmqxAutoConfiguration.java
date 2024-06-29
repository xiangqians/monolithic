package org.xiangqian.monolithic.common.emqx;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiangqian
 * @date 21:54 2024/06/28
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties({EmqxProperties.class})
public class EmqxAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(Emqx.class)
    public Emqx emqx(EmqxProperties emqxProperties) {
        return new Emqx(emqxProperties);
    }

}
