package org.xiangqian.monolithic.util.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;

/**
 * @author xiangqian
 * @date 12:21 2024/06/10
 */
public class RedisSetFactory {

    private SetOperations<String, Object> setOperations;

    public RedisSetFactory(RedisTemplate<String, Object> redisTemplate) {
        this.setOperations = redisTemplate.opsForSet();
    }

    public RedisSet get(String key) {
        return new RedisSet(setOperations, key);
    }

}
