package org.xiangqian.monolithic.scheduler.biz;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.xiangqian.monolithic.scheduler.Task;

import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 17:15 2024/06/23
 */
@Slf4j
@Component
public class FirstTask extends Task {

    @Override
    protected void execute() throws Throwable {
        log.debug("【1】开始执行任务 ....");
        TimeUnit.SECONDS.sleep(1);

        if (false) {
            throw new NullPointerException("空指针异常 1/0");
        }

        log.debug("【1】执行任务中 ....");
        TimeUnit.SECONDS.sleep(1);

        log.debug("【1】执行任务结束");
    }

}
