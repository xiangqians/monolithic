package org.xiangqian.monolithic.common.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreMutex;

/**
 * 分布式不可重入互斥锁
 * 与分布式可重入互斥锁不同的是，它不支持重入，同一个线程在未释放锁之前无法再次获取。
 *
 * @author xiangqian
 * @date 23:43 2024/07/12
 */
public class ZookeeperNonReentrantLock extends ZookeeperLock {

    ZookeeperNonReentrantLock(InterProcessSemaphoreMutex interProcessSemaphoreMutex) {
        super(interProcessSemaphoreMutex);
    }

    ZookeeperNonReentrantLock(CuratorFramework curatorFramework, String path) {
        this(new InterProcessSemaphoreMutex(curatorFramework, path));
    }

}
