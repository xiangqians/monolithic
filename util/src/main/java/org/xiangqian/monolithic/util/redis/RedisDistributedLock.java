package org.xiangqian.monolithic.util.redis;

import java.util.concurrent.TimeUnit;

/**
 * Redis分布式锁
 * <p>
 * 分布式锁：控制分布式系统不同进程共同访问共享资源的一种锁的实现。如果不同的系统或同一个系统的不同主机之间共享了某个临界资源，往往需要互斥来防止彼此干扰，以保证一致性。
 * <p>
 * 分布式锁特征：
 * 1、互斥性: 任意时刻，只有一个客户端能持有锁
 * 2、锁超时释放：持有锁超时，可以释放，防止不必要的资源浪费，也可以防止死锁
 * 3、可重入性：一个线程如果获取了锁之后，可以再次对其请求加锁
 * 4、高性能和高可用：加锁和解锁需要开销尽可能低，同时也要保证高可用，避免分布式锁失效
 * 5、安全性：锁只能被持有的客户端删除，不能被其他客户端删除
 *
 * @author xiangqian
 * @date 11:52 2024/06/10
 */
public interface RedisDistributedLock {

    /**
     * 阻塞式获取锁
     * 如果锁已被其他线程或客户端持有，则当前线程将被阻塞，直到获取到锁为止
     */
    void lock();

    /**
     * 阻塞式获取锁
     * 如果锁已被其他线程或客户端持有，则当前线程将被阻塞，直到获取到锁为止
     *
     * @param leaseTime 持有锁时间
     * @param timeUnit  时间单位
     */
    void lock(long leaseTime, TimeUnit timeUnit);

    /**
     * 尝试非阻塞式获取锁
     * 如果锁当前没有被任何线程或客户端持有，则立即获取锁并返回true，否则返回false
     *
     * @return
     */
    boolean tryLock();

    /**
     * 尝试在指定的等待时间内非阻塞式获取锁
     * 如果在等待时间内获取到了锁，则返回true，否则返回false
     *
     * @param waitTime  最多等待锁时间
     * @param leaseTime 持有锁时间
     * @param timeUnit  时间单位
     */
    boolean tryLock(long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException;

    /**
     * 释放锁
     * 如果当前线程持有锁，则释放锁，否则抛出 {@link IllegalMonitorStateException} 异常
     */
    void unlock();

    /**
     * 强制释放锁，不管当前线程是否持有锁，都会释放
     */
    void forceUnlock();

}
