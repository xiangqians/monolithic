package org.xiangqian.monolithic.biz;

import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.xiangqian.monolithic.util.Redis;

/**
 * Redis配置
 * <p>
 * RedisTemplate
 * {@link org.springframework.data.redis.core.RedisTemplate}
 * {@link org.springframework.data.redis.core.ReactiveRedisTemplate}（响应式）
 * <p>
 * RedissonClient
 * {@link org.redisson.api.RedissonClient}
 * {@link org.redisson.api.RedissonRxClient}（响应式）
 * {@link org.redisson.api.RedissonReactiveClient}（响应式）
 *
 * @author xiangqian
 * @date 11:57 2024/06/08
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureBefore({org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class, RedissonAutoConfigurationV2.class})
public class RedisAutoConfiguration {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        RedisSerializer<?> valueSerializer = new GenericToStringSerializer<>(Object.class);

        // 使用 Jackson 提供的通用 Serializer
//        GenericJackson2JsonRedisSerializer valueSerializer = new GenericJackson2JsonRedisSerializer(JsonUtil.OBJECT_MAPPER);

        // String 类型的 key/value 序列化器
        redisTemplate.setKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setValueSerializer(valueSerializer);

        // Hash 类型的 key/value 序列化器
        redisTemplate.setHashKeySerializer(StringRedisSerializer.UTF_8);
        redisTemplate.setHashValueSerializer(valueSerializer);

        // 初始化RedisTemplate
        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }

    @Bean
    public Redis redis(RedisTemplate<String, Object> redisTemplate, RedissonClient redissonClient) {
        return new Redis(redisTemplate, redissonClient);
    }

}
