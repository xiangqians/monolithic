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
import org.xiangqian.monolithic.common.db.sched.entity.TaskEntity;
import org.xiangqian.monolithic.common.db.sched.mapper.TaskMapper;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author xiangqian
 * @date 21:12 2024/06/20
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
public class SchedulerConfiguration implements ApplicationRunner {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public DefaultTaskScheduler taskScheduler() {
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

        return new DefaultTaskScheduler(threadPoolTaskScheduler);
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        DefaultTaskScheduler taskScheduler = applicationContext.getBean(DefaultTaskScheduler.class);
        TaskMapper taskMapper = applicationContext.getBean(TaskMapper.class);
        List<TaskEntity> taskEntities = taskMapper.selectList(new LambdaQueryWrapper<TaskEntity>());
        for (TaskEntity taskEntity : taskEntities) {
            Task task = (Task) applicationContext.getBean(taskEntity.getBean());
            task.setTaskEntity(taskEntity);
            taskScheduler.schedule(task, new CronTrigger(taskEntity.getCron()));
        }
    }

}
