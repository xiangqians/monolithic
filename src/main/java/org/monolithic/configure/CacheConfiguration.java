package org.monolithic.configure;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @author xiangqian
 * @date 23:11 2022/08/16
 */
@Configuration
@EnableCaching // 开启基于注解缓存
public class CacheConfiguration {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setCaffeine(Caffeine.newBuilder()
                // 初始缓存容量
                .initialCapacity(1024)
                // 设置写后过期时间
                .expireAfterWrite(Duration.ofMinutes(5))
                // 设置访问后过期时间
//                .expireAfterAccess(Duration.ofMinutes(1))
                // 设置cache的最大缓存数量
                .maximumSize(1024 * 1024));

        // 设置缓存加载器
        cacheManager.setCacheLoader(key -> null);

        // 不允许空值
        cacheManager.setAllowNullValues(false);

        return cacheManager;
    }

}
