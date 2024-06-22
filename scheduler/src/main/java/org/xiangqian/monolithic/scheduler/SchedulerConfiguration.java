package org.xiangqian.monolithic.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 21:12 2024/06/20
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class SchedulerConfiguration implements ApplicationRunner {

    @Autowired
    private ApplicationContext applicationContext;

    private TaskScheduler taskScheduler;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        // 设置线程池大小
        // 获取了CPU核心数
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        // 设置线程池大小为 可用处理器数 * 2
        int poolSize = availableProcessors * 2;
        threadPoolTaskScheduler.setPoolSize(poolSize);

        // 设置线程名前缀
        threadPoolTaskScheduler.setThreadNamePrefix("TaskScheduler-");

        // 设置等待所有任务完成的时间
        threadPoolTaskScheduler.setAwaitTerminationSeconds(60);

        // 设置在关闭时等待所有任务完成
        threadPoolTaskScheduler.setWaitForTasksToCompleteOnShutdown(true);

        // 设置拒绝策略
        threadPoolTaskScheduler.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        threadPoolTaskScheduler.initialize();

        return new TaskScheduler(threadPoolTaskScheduler);
    }

    private void test() {
        ScheduledFuture scheduledFuture = taskScheduler.schedule(new Task() {
            @Override
            protected void execute() throws Throwable {
                log.debug("-----【TaskScheduler-1】每隔2秒执行一次--------");
//                TimeUnit.SECONDS.sleep(10);
            }
        }, new CronTrigger("0 19 * * * ?"));

        new Thread(() -> {
            while (true) {
                try {
                    log.debug("任务状态" +
                                    "\n\t任务是否已经完成：{}" +
                                    "\n\t任务是否已被取消：{}" +
                                    "\n\t任务是否正在运行中：{}" +
                                    "\n\t任务是否已经停止：{}",
                            taskScheduler.isDone(scheduledFuture),
                            taskScheduler.isCancelled(scheduledFuture),
                            taskScheduler.isRunning(scheduledFuture),
                            taskScheduler.isStopped(scheduledFuture));

                    TimeUnit.SECONDS.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });//.start();
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        taskScheduler = applicationContext.getBean(TaskScheduler.class);
        log.debug("taskScheduler===={}", taskScheduler);
        test();
    }

}
