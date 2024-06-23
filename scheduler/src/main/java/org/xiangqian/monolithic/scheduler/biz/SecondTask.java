package org.xiangqian.monolithic.scheduler.biz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xiangqian.monolithic.scheduler.Task;

import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 17:16 2024/06/23
 */
@Slf4j
@Component
public class SecondTask extends Task {

    @Override
    protected void execute() throws Throwable {
        log.debug("【2】开始执行任务 ....");
        TimeUnit.SECONDS.sleep(1);

        log.debug("【2】执行任务中 ....");
        TimeUnit.SECONDS.sleep(1);

        log.debug("【2】执行任务结束");
    }

}
