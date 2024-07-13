package org.xiangqian.monolithic.common.zookeeper;

import org.xiangqian.monolithic.common.util.time.DateTimeUtil;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 21:36 2024/07/12
 */
public class ZookeeperLockTest extends ZookeeperTest {

    protected Task task = () -> TimeUnit.SECONDS.sleep(2);

    protected void lock(ZookeeperLock zookeeperLock, Duration timeout, Task task) throws Exception {
        try {
            System.out.format("%s [%s, %s] 获取锁 ...", DateTimeUtil.format(LocalDateTime.now()), Thread.currentThread().getId(), Thread.currentThread().getName()).println();
            if (timeout != null) {
                if (!zookeeperLock.acquire(timeout)) {
                    System.out.format("%s [%s, %s] 无法获取锁！", DateTimeUtil.format(LocalDateTime.now()), Thread.currentThread().getId(), Thread.currentThread().getName()).println();
                    zookeeperLock = null;
                    return;
                }
            } else {
                zookeeperLock.acquire();
            }
            System.out.format("%s [%s, %s] 获取到锁！", DateTimeUtil.format(LocalDateTime.now()), Thread.currentThread().getId(), Thread.currentThread().getName()).println();
            task.execute();
        } finally {
            if (zookeeperLock != null) {
                System.out.format("%s [%s, %s] 释放锁！", DateTimeUtil.format(LocalDateTime.now()), Thread.currentThread().getId(), Thread.currentThread().getName()).println();
                zookeeperLock.release();
            }
        }
    }

}
