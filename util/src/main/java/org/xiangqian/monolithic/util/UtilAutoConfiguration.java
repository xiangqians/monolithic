package org.xiangqian.monolithic.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * @author xiangqian
 * @date 22:57 2024/06/07
 */
@Configuration(proxyBeanMethods = false)
public class UtilAutoConfiguration {

    @Bean
    @ConditionalOnBean(JavaMailSender.class)
    public Mail mail(JavaMailSender javaMailSender, @Value("${spring.mail.from}") String from) {
        return new Mail(javaMailSender, from);
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public Redis redis(RedisTemplate<String, Object> redisTemplate) {
        return new Redis(redisTemplate);
    }

}
