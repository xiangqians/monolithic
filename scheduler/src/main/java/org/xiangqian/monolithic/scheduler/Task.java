package org.xiangqian.monolithic.scheduler;

import lombok.extern.slf4j.Slf4j;

/**
 * @author xiangqian
 * @date 21:57 2024/06/19
 */
@Slf4j
public abstract class Task implements Runnable {

    @Override
    public void run() {
        try {
            log.debug("开始调度任务 ...");

            execute();
        } catch (Throwable t) {
            log.error("", t);
        } finally {
            log.debug("调度任务结束！");
        }
    }

    protected abstract void execute() throws Throwable;

}
