package org.xiangqian.monolithic.util.redis;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import java.util.concurrent.TimeUnit;

/**
 * 多机实现的分布式锁 Redisson + RedLock
 *
 * @author xiangqian
 * @date 12:31 2024/06/10
 */
public class RedisDistributedLockImpl implements RedisDistributedLock {

    private RLock rLock;

    public RedisDistributedLockImpl(RedissonClient redissonClient, String key) {
        this.rLock = redissonClient.getLock(key);
    }

    @Override
    public void lock() {
        rLock.lock();
    }

    @Override
    public void lock(long leaseTime, TimeUnit timeUnit) {
        rLock.lock(leaseTime, timeUnit);
    }

    @Override
    public boolean tryLock() {
        return rLock.tryLock();
    }

    @Override
    public boolean tryLock(long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException {
        return rLock.tryLock(waitTime, leaseTime, timeUnit);
    }

    @Override
    public void unlock() {
        rLock.unlock();
    }

    @Override
    public void forceUnlock() {
        rLock.forceUnlock();
    }

}
