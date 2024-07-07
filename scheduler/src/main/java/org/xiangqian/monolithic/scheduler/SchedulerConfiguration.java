package org.xiangqian.monolithic.scheduler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.xiangqian.monolithic.common.mysql.sched.entity.TaskEntity;
import org.xiangqian.monolithic.common.mysql.sched.entity.TaskRecordEntity;
import org.xiangqian.monolithic.common.mysql.sched.mapper.TaskMapper;
import org.xiangqian.monolithic.common.mysql.sched.mapper.TaskRecordMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xiangqian
 * @date 21:12 2024/06/20
 */
@Configuration(proxyBeanMethods = false)
public class SchedulerConfiguration implements ApplicationRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public Scheduler taskScheduler() {
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

        return new Scheduler(threadPoolTaskScheduler);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Scheduler taskScheduler = applicationContext.getBean(Scheduler.class);
        TaskMapper taskMapper = applicationContext.getBean(TaskMapper.class);
        TaskRecordMapper taskRecordMapper = applicationContext.getBean(TaskRecordMapper.class);
        List<TaskEntity> taskEntities = taskMapper.selectList(new LambdaQueryWrapper<TaskEntity>());
        for (TaskEntity taskEntity : taskEntities) {
            Runnable task = (Runnable) applicationContext.getBean(Class.forName(taskEntity.getClazz()));
            taskScheduler.schedule(new Task(taskRecordMapper, taskEntity, task), new CronTrigger(taskEntity.getCron()));
        }
    }

    @Slf4j
    public static class Task implements Runnable {
        private TaskRecordMapper taskRecordMapper;

        private TaskEntity taskEntity;
        private TaskRecordEntity taskRecordEntity;

        private Runnable task;

        public Task(TaskRecordMapper taskRecordMapper, TaskEntity taskEntity, Runnable task) {
            this.taskRecordMapper = taskRecordMapper;
            this.taskEntity = taskEntity;
            this.task = task;
        }

        @Override
        public void run() {
            try {
                taskRecordEntity = new TaskRecordEntity();
                taskRecordEntity.setTaskId(taskEntity.getId());
                taskRecordEntity.setStatus((byte) 0);
                taskRecordEntity.setStartTime(LocalDateTime.now());
                taskRecordMapper.insert(taskRecordEntity);

                task.run();

                taskRecordEntity.setStatus((byte) 1);
                taskRecordEntity.setEndTime(LocalDateTime.now());
                taskRecordMapper.updateById(taskRecordEntity);
            } catch (Throwable t) {
                log.error(String.format("执行任务【%s，%s】发生异常", taskEntity.getId(), taskEntity.getName()), t);
                taskRecordEntity.setStatus((byte) 2);
                taskRecordEntity.setEndTime(LocalDateTime.now());
                taskRecordEntity.setMsg(t.toString());
                taskRecordMapper.updateById(taskRecordEntity);
            }
        }
    }

}
