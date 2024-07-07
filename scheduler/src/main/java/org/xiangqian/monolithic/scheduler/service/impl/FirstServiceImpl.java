package org.xiangqian.monolithic.scheduler.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.scheduler.service.FirstService;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * @author xiangqian
 * @date 17:15 2024/06/23
 */
@Slf4j
@Service
public class FirstServiceImpl implements FirstService {

    private Random random = new Random();

    @SneakyThrows
    @Override
    public void run() {
        log.debug("【任务1】开始执行任务 ....");
        TimeUnit.SECONDS.sleep(1);

        if (random.nextInt(10) <= 3) {
            throw new NullPointerException("空指针异常 1/0");
        }

        log.debug("【任务1】执行任务结束");
    }

}
