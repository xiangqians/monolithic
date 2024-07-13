package org.xiangqian.monolithic.common.zookeeper;

import lombok.SneakyThrows;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 21:43 2024/07/12
 */
public class ZookeeperReadWriteLockTest extends ZookeeperLockTest {

    private Task task = () -> TimeUnit.SECONDS.sleep(10);

    private ZookeeperReadWriteLock getZookeeperReadWriteLock() {
        return zookeeper.readWriteLock("/test-Read-Write-lock");
    }

    private void readLock(ZookeeperReadWriteLock zookeeperReadWriteLock, Duration timeout) throws Exception {
        lock(zookeeperReadWriteLock.readLock(), timeout, task);
    }

    private void writeLock(ZookeeperReadWriteLock zookeeperReadWriteLock, Duration timeout) throws Exception {
        lock(zookeeperReadWriteLock.writeLock(), timeout, task);
    }

    @Test
    @SneakyThrows
    public void testReadLock1() {
        readLock(getZookeeperReadWriteLock(), Duration.ofSeconds(10));
    }

    @Test
    @SneakyThrows
    public void testReadLock2() {
        readLock(getZookeeperReadWriteLock(), Duration.ofSeconds(10));
    }

    @Test
    @SneakyThrows
    public void testWriteLock1() {
        writeLock(getZookeeperReadWriteLock(), Duration.ofSeconds(10));
    }

    @Test
    @SneakyThrows
    public void testWriteLock2() {
        writeLock(getZookeeperReadWriteLock(), Duration.ofSeconds(10));
    }

}
