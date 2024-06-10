package org.xiangqian.monolithic.util.redis;

import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author xiangqian
 * @date 12:19 2024/06/10
 */
public class RedisHashFactory {

    private HashOperations<String, Object, Object> hashOperations;

    public RedisHashFactory(RedisTemplate<String, Object> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    public RedisHash get(String key) {
        return new RedisHash(hashOperations, key);
    }

}
