package org.xiangqian.monolithic.rsocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author xiangqian
 * @date 12:00 2024/06/30
 */
@Configuration(proxyBeanMethods = false)
public class RsocketAutoConfiguration {

    @Bean
    public Rsocket rsocket() {
        return new Rsocket();
    }

}
