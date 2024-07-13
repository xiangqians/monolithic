package org.xiangqian.monolithic.common.zookeeper;

import lombok.SneakyThrows;
import org.junit.Test;

import java.time.Duration;

/**
 * @author xiangqian
 * @date 21:35 2024/07/12
 */
public class ZookeeperNonReentrantLockTest extends ZookeeperLockTest {

    private ZookeeperNonReentrantLock getZookeeperNonReentrantLock() {
        return zookeeper.nonReentrantLock("/test-non-reentrant-lock");
    }

    @Test
    @SneakyThrows
    public void test1() {
        lock(getZookeeperNonReentrantLock(), null, task);
    }

    @Test
    @SneakyThrows
    public void test2() {
        new Thread((Task) () -> lock(getZookeeperNonReentrantLock(), null, task)).start();
        lock(getZookeeperNonReentrantLock(), Duration.ofSeconds(10), task);
    }


    @Test
    @SneakyThrows
    public void test3() {
        ZookeeperNonReentrantLock zookeeperNonReentrantLock = getZookeeperNonReentrantLock();
        lock(zookeeperNonReentrantLock, null, () -> {
            lock(zookeeperNonReentrantLock, Duration.ofSeconds(10), task);
            task.execute();
        });
    }

}
