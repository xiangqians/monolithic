package org.xiangqian.monolithic.util;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.xiangqian.monolithic.util.redis.*;

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
    public RedisKey redisKey(RedisTemplate<String, Object> redisTemplate) {
        return new RedisKey(redisTemplate);
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public RedisString redisString(RedisTemplate<String, Object> redisTemplate) {
        return new RedisString(redisTemplate);
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public RedisListFactory redisListFactory(RedisTemplate<String, Object> redisTemplate) {
        return new RedisListFactory(redisTemplate);
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public RedisSetFactory redisSetFactory(RedisTemplate<String, Object> redisTemplate) {
        return new RedisSetFactory(redisTemplate);
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public RedisHashFactory redisHashFactory(RedisTemplate<String, Object> redisTemplate) {
        return new RedisHashFactory(redisTemplate);
    }

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public RedisDistributedLockFactory redisDistributedLockFactory(RedissonClient redissonClient) {
        return new RedisDistributedLockFactory(redissonClient);
    }

}
