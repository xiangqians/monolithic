package org.xiangqian.monolithic.common.redis;

import org.redisson.api.RedissonClient;
import org.redisson.spring.starter.RedissonAutoConfigurationV2;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
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
        return Redis.createRedisTemplate(redisConnectionFactory);
    }

    @Bean
    public Redis redis(RedisTemplate<String, Object> redisTemplate, RedissonClient redissonClient) {
        return new Redis(redisTemplate, redissonClient);
    }

}
