package org.xiangqian.monolithic.biz;

import org.redisson.api.RedissonClient;
import org.redisson.spring.data.connection.RedissonConnectionFactory;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.xiangqian.monolithic.util.Redis;

/**
 * @author xiangqian
 * @date 11:57 2024/06/08
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore({org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class, RedissonAutoConfigurationV2.class})
public class RedisAutoConfiguration {

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory redisConnectionFactory) {
        return null;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 设置key序列化器
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // 设置value序列化器
        redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Object.class));

        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 设置hash key序列化器
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 设置hash value序列化器
        redisTemplate.setHashValueSerializer(new GenericToStringSerializer<>(Object.class));

        // 初始化RedisTemplate
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    public Redis redis(RedisTemplate<String, Object> redisTemplate, RedissonClient redissonClient) {
        return new Redis(redisTemplate, redissonClient);
    }

}
