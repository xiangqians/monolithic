package org.xiangqian.monolithic.common.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;

/**
 * 分布式可重入互斥锁
 * 分布式可重入互斥锁，是 Curator 最常用的锁类型之一。它的特点是可以重入，即同一个线程可以多次获取同一个锁，同时还支持超时机制。
 * 用于在分布式环境中实现对共享资源的安全访问，当一个客户端持有该锁时，其他客户端请求锁会被阻塞，直到持有锁的客户端释放锁为止。
 *
 * @author xiangqian
 * @date 21:05 2024/02/28
 */
public class ZookeeperReentrantLock extends ZookeeperLock {

    ZookeeperReentrantLock(InterProcessMutex interProcessMutex) {
        super(interProcessMutex);
    }

    ZookeeperReentrantLock(CuratorFramework curatorFramework, String path) {
        this(new InterProcessMutex(curatorFramework, path));
    }

}