package org.xiangqian.monolithic.util;

import lombok.SneakyThrows;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 21:00 2024/06/20
 */
public class ThreadPoolExecutorTest {

    @SneakyThrows
    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = ThreadPoolExecutor.create();
        new Thread(() -> {
            while (!threadPoolExecutor.isTerminated()) {
                try {
                    System.out.println(threadPoolExecutor);
                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        threadPoolExecutor.execute(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println("执行任务中 ...");
                TimeUnit.SECONDS.sleep(5);
                System.out.println("执行任务完成！");
            }
        });

        // 优雅关闭线程池
        threadPoolExecutor.shutdown();
        try {
            if (!threadPoolExecutor.awaitTermination(Duration.ofSeconds(60))) {
                threadPoolExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            threadPoolExecutor.shutdownNow();
            // 恢复中断状态
            Thread.currentThread().interrupt();
        }

        System.out.println("线程池已关闭！");
    }

}
