package org.xiangqian.monolithic.task.scheduler;

import org.junit.Test;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.Trigger;
import org.xiangqian.monolithic.common.util.CronUtil;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledFuture;

/**
 * @author xiangqian
 * @date 21:25 2024/06/20
 */
public class CronTest {

    @Test
    public void test() {
        // 每10秒钟执行一次
        String expression = "0/10 * * * * ?";
        CronUtil.printNextNExecutionTimes(expression, 10);
    }

}
