package org.xiangqian.monolithic.common.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 21:05 2024/02/28
 */
public class ZookeeperLock {

    // InterProcessMutex 是 Apache Curator 提供的一种分布式可重入互斥锁实现，用于在分布式环境中实现对共享资源的安全访问。
    // 当一个客户端持有该锁时，其他客户端请求锁会被阻塞，直到持有锁的客户端释放锁为止。
    private InterProcessMutex interProcessMutex;

    ZookeeperLock(CuratorFramework curatorFramework, String path) {
        this.interProcessMutex = new InterProcessMutex(curatorFramework, path);
    }

    /**
     * 获取锁
     * 如果锁当前不可用，将一直等待，直到获取锁为止。
     *
     * @throws Exception
     */
    public void acquire() throws Exception {
        interProcessMutex.acquire();
    }

    /**
     * 尝试获取锁
     * 如果等待指定时间仍未获取到锁则返回 false
     *
     * @param timeout
     * @return
     * @throws Exception
     */
    public boolean acquire(Duration timeout) throws Exception {
        return interProcessMutex.acquire(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    /**
     * 释放锁
     *
     * @throws Exception
     */
    public void release() throws Exception {
        // 在Curator的InterProcessMutex类中，isOwnedByCurrentThread()方法和isAcquiredInThisProcess()方法都是用于检查当前线程或进程是否已经获取了特定的分布式锁。
        // 1、isOwnedByCurrentThread()方法用于检查当前线程是否已经获取了指定的分布式锁。
        // 如果当前线程已经成功获取了该分布式锁，则该方法返回 true，表示当前线程拥有该锁；
        // 如果当前进线程有获取该分布式锁，或者之前获取的锁已经被释放，那么该方法会返回 false。
        // 这个方法主要用于检查当前线程是否持有该分布式锁，在多线程环境中可以帮助你确保某些操作只能由持有该锁的线程执行。
        // 2、isAcquiredInThisProcess()方法用于检查当前进程是否已经获取了指定的分布式锁。具体来说，这个方法会检查当前进程是否已经成功获取了InterProcessMutex对象所表示的分布式锁。
        // 如果当前进程已经成功获取了该分布式锁，则该方法返回true，表示当前进程拥有该锁；
        // 如果当前进程没有获取该分布式锁，或者之前获取的锁已经被释放，那么该方法会返回 false。
        // 这个方法与isOwnedByCurrentThread()方法类似，不同之处在于它是针对进程而非线程的。它可以用于检查当前进程是否持有特定的分布式锁。
        if (interProcessMutex.isAcquiredInThisProcess()) {
            // 释放锁
            interProcessMutex.release();
        }
    }

}