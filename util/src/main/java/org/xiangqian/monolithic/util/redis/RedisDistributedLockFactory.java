package org.xiangqian.monolithic.util.redis;

import org.redisson.api.RedissonClient;

/**
 * @author xiangqian
 * @date 12:59 2024/06/10
 */
public class RedisDistributedLockFactory {

    private RedissonClient redissonClient;

    public RedisDistributedLockFactory(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    public RedisDistributedLock get(String key) {
        return new RedisDistributedLockImpl(redissonClient, key);
    }

}
