package org.xiangqian.monolithic.common.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessReadWriteLock;

/**
 * 分布式读写锁
 * 允许多个客户端同时读取共享资源，但只允许一个客户端进行写操作。
 * 读锁是共享的，写锁是排他的。
 *
 * @author xiangqian
 * @date 21:47 2024/07/12
 */
public class ZookeeperReadWriteLock {

    private InterProcessReadWriteLock interProcessReadWriteLock;
    private ZookeeperReentrantLock zookeeperReadLock;
    private ZookeeperReentrantLock zookeeperWriteLock;

    ZookeeperReadWriteLock(CuratorFramework curatorFramework, String path) {
        this.interProcessReadWriteLock = new InterProcessReadWriteLock(curatorFramework, path);
    }

    public ZookeeperReentrantLock readLock() {
        if (zookeeperReadLock == null) {
            zookeeperReadLock = new ZookeeperReentrantLock(interProcessReadWriteLock.readLock());
        }
        return zookeeperReadLock;
    }

    public ZookeeperReentrantLock writeLock() {
        if (zookeeperWriteLock == null) {
            zookeeperWriteLock = new ZookeeperReentrantLock(interProcessReadWriteLock.writeLock());
        }
        return zookeeperWriteLock;
    }

}
