package org.xiangqian.monolithic.common.zookeeper;

import lombok.SneakyThrows;
import org.junit.Test;

import java.time.Duration;

/**
 * @author xiangqian
 * @date 23:53 2024/07/12
 */
public class ZookeeperReentrantLockTest extends ZookeeperLockTest {

    private ZookeeperReentrantLock getZookeeperReentrantLock() {
        return zookeeper.reentrantLock("/test-reentrant-lock");
    }

    @Test
    @SneakyThrows
    public void test1() {
        lock(getZookeeperReentrantLock(), null, task);
    }

    @Test
    @SneakyThrows
    public void test2() {
        new Thread((Task) () -> lock(getZookeeperReentrantLock(), null, task)).start();
        lock(getZookeeperReentrantLock(), Duration.ofSeconds(10), task);
    }

    @Test
    @SneakyThrows
    public void test3() {
        ZookeeperReentrantLock zookeeperReentrantLock = getZookeeperReentrantLock();
        lock(zookeeperReentrantLock, null, () -> {
            lock(zookeeperReentrantLock, Duration.ofSeconds(10), task);
            task.execute();
        });
    }

}
