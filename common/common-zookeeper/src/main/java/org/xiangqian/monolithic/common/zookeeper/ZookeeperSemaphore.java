package org.xiangqian.monolithic.common.zookeeper;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessSemaphoreV2;
import org.apache.curator.framework.recipes.locks.Lease;

import java.time.Duration;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * 分布式信号量
 * 类似于 Java 中的信号量（{@link Semaphore}）概念，可以用来控制同时访问特定资源的客户端数量。
 * <p>
 * 如果需要简单的公平锁且不涉及锁的可重入性，可以选择 InterProcessSemaphoreMutex。
 * 如果需要支持可重入性或者更复杂的锁管理机制，可以考虑使用 InterProcessSemaphoreV2。
 *
 * @author xiangqian
 * @date 21:56 2024/07/12
 */
public class ZookeeperSemaphore {

    private InterProcessSemaphoreV2 interProcessSemaphoreV2;

    ZookeeperSemaphore(InterProcessSemaphoreV2 interProcessSemaphoreV2) {
        this.interProcessSemaphoreV2 = interProcessSemaphoreV2;
    }

    /**
     * @param curatorFramework
     * @param path             节点路径
     * @param maxLeases        最大租约数
     */
    ZookeeperSemaphore(CuratorFramework curatorFramework, String path, int maxLeases) {
        this(new InterProcessSemaphoreV2(curatorFramework, path, maxLeases));
    }

    public Lease acquire() throws Exception {
        return interProcessSemaphoreV2.acquire();
    }

    public Lease acquire(Duration timeout) throws Exception {
        return interProcessSemaphoreV2.acquire(timeout.toMillis(), TimeUnit.MILLISECONDS);
    }

    public void release(Lease lease) throws Exception {
        interProcessSemaphoreV2.returnLease(lease);
    }

}
