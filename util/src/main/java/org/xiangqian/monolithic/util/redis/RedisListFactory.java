package org.xiangqian.monolithic.util.redis;

import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author xiangqian
 * @date 12:11 2024/06/10
 */
public class RedisListFactory {

    private ListOperations<String, Object> listOperations;

    public RedisListFactory(RedisTemplate<String, Object> redisTemplate) {
        this.listOperations = redisTemplate.opsForList();
    }

    public RedisList get(String key) {
        return new RedisList(listOperations, key);
    }

}
