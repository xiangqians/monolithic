package org.xiangqian.monolithic.task.scheduler.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.task.scheduler.service.SecondService;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 17:16 2024/06/23
 */
@Slf4j
@Service
public class SecondServiceImpl implements SecondService {

    private Random random = new Random();

    @SneakyThrows
    @Override
    public void run() {
        log.debug("【任务2】开始执行任务 ....");
        TimeUnit.SECONDS.sleep(1);

        if (random.nextInt(10) <= 3) {
            Class.forName("org.example.ExampleTest");
        }

        log.debug("【任务2】执行任务结束");
    }

}
