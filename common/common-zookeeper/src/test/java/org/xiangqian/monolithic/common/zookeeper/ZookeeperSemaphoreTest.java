package org.xiangqian.monolithic.common.zookeeper;

import lombok.SneakyThrows;
import org.apache.curator.framework.recipes.locks.Lease;
import org.junit.Test;
import org.xiangqian.monolithic.common.util.time.DateTimeUtil;

import java.time.LocalDateTime;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 21:58 2024/07/12
 */
public class ZookeeperSemaphoreTest extends ZookeeperTest {

    @Test
    @SneakyThrows
    public void test1() {
        // 允许同时一个线程访问资源
        Semaphore semaphore = new Semaphore(1);

        int count = 10;
        CountDownLatch countDownLatch = new CountDownLatch(count);

        Task task = () -> {
            try {
                System.out.format("%s [%s, %s] 获取锁 ...", DateTimeUtil.format(LocalDateTime.now()), Thread.currentThread().getId(), Thread.currentThread().getName()).println();
                semaphore.acquire();
                System.out.format("%s [%s, %s] 获取到锁！", DateTimeUtil.format(LocalDateTime.now()), Thread.currentThread().getId(), Thread.currentThread().getName()).println();
                TimeUnit.SECONDS.sleep(2);
            } finally {
                System.out.format("%s [%s, %s] 释放锁！", DateTimeUtil.format(LocalDateTime.now()), Thread.currentThread().getId(), Thread.currentThread().getName()).println();
                semaphore.release();
                countDownLatch.countDown();
            }
        };

        // 创建多个线程来模拟对共享资源的访问
        for (int i = 0; i < count; i++) {
            new Thread(task, "线程-" + i).start();
        }

        countDownLatch.await();
    }

    @Test
    @SneakyThrows
    public void test2() {
        int count = 10;
        CountDownLatch countDownLatch = new CountDownLatch(count);

        Task task = () -> {
            ZookeeperSemaphore zookeeperSemaphore = zookeeper.semaphore("/test-semaphore", 1);
            Lease lease = null;
            try {
                System.out.format("%s [%s, %s] 获取锁 ...", DateTimeUtil.format(LocalDateTime.now()), Thread.currentThread().getId(), Thread.currentThread().getName()).println();
                lease = zookeeperSemaphore.acquire();
                System.out.format("%s [%s, %s] 获取到锁！", DateTimeUtil.format(LocalDateTime.now()), Thread.currentThread().getId(), Thread.currentThread().getName()).println();
                TimeUnit.SECONDS.sleep(2);
            } finally {
                System.out.format("%s [%s, %s] 释放锁！", DateTimeUtil.format(LocalDateTime.now()), Thread.currentThread().getId(), Thread.currentThread().getName()).println();
                zookeeperSemaphore.release(lease);
                countDownLatch.countDown();
            }
        };

        // 创建多个线程来模拟对共享资源的访问
        for (int i = 0; i < count; i++) {
            new Thread(task, "线程-" + i).start();
        }

        countDownLatch.await();
    }

}
