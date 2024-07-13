package org.xiangqian.monolithic.common.zookeeper;

import lombok.SneakyThrows;

/**
 * @author xiangqian
 * @date 21:35 2024/07/12
 */
public interface Task extends Runnable {

    @SneakyThrows
    @Override
    default void run() {
        execute();
    }

    void execute() throws Exception;

}
