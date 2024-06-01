package org.xiangqian.monolithic.biz;

import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

/**
 * @author xiangqian
 * @date 17:07 2024/06/01
 */
@Configuration(proxyBeanMethods = false)
@MapperScan("org.xiangqian.monolithic.biz.*.mapper") // 指定要扫描的mapper包路径
@ComponentScan("org.xiangqian.monolithic.biz.*.service.impl") // 扫描组件
@AutoConfigureBefore(org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration.class)
public class BizAutoConfiguration {

    /**
     * 配置MybatisPlus分页拦截器
     *
     * @return
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor();
        mybatisPlusInterceptor.setInterceptors(List.of(paginationInnerInterceptor));
        return mybatisPlusInterceptor;
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
    public Redis redis(RedisTemplate<String, Object> redisTemplate) {
        return new Redis(redisTemplate);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
