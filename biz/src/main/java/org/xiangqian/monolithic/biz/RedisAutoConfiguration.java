package org.xiangqian.monolithic.biz;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * {@link org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration}
 *
 * @author xiangqian
 * @date 19:58 2024/05/30
 */
@AutoConfigureBefore(org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class RedisAutoConfiguration {

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
    public Redis redis(RedisTemplate<String, Object> redisTemplate) {
        return new Redis(redisTemplate);
    }

}
