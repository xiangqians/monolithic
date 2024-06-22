package org.xiangqian.monolithic.web.sys.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.biz.LazyList;
import org.xiangqian.monolithic.biz.Page;
import org.xiangqian.monolithic.web.sys.entity.LogEntity;
import org.xiangqian.monolithic.web.sys.mapper.LogMapper;
import org.xiangqian.monolithic.web.sys.service.LogService;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
@Slf4j
@Service
public class LogServiceImpl implements LogService, Runnable, ApplicationRunner {

    @Autowired
    private LogMapper logMapper;

    // 阻塞式队列
    private BlockingQueue<LogEntity> blockingQueue;

    public LogServiceImpl() {
        // 基于数组的有界阻塞队列
        this.blockingQueue = new ArrayBlockingQueue<>(1024 * 1024);
    }

    @Override
    public LazyList<LogEntity> list(LazyList<LogEntity> list, LogEntity log) {
        return logMapper.list(list, log);
    }

    @Override
    public Page<LogEntity> page(Page<LogEntity> page, LogEntity log) {
        return logMapper.page(page, log);
    }

    @Override
    public void asyncSave(LogEntity log) {
        // 尝试将指定的元素插入此队列
        // 如果插入成功则返回 true，如果队列已满则抛出 IllegalStateException
//        blockingQueue.add(log);

        // 尝试将指定的元素插入此队列
        // 如果插入成功则返回 true，如果队列已满则返回 false
        if (!blockingQueue.offer(log)) {
            this.log.warn("日志阻塞式队列已满，无法添加日志信息：{}", log);
        }

        // 尝试在指定的时间内将指定的元素插入此队列
        // 如果插入成功则返回 true，如果在超时时间内仍无法插入则返回 false
//        blockingQueue.offer(log, 30L, TimeUnit.SECONDS);

        // 将指定的元素插入此队列，如果队列已满则等待直到有空间为止
//        blockingQueue.put(log);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 获取但不移除此队列的头部元素
                // 如果队列为空则抛出 NoSuchElementException
//                LogEntity log = blockingQueue.element();

                // 获取但不移除此队列的头部元素
                // 如果队列为空则返回 null
//                LogEntity log = blockingQueue.peek();

                // --------------

                // 获取并移除此队列的头部元素
                // 如果队列为空则抛出 NoSuchElementException。
//                LogEntity log = blockingQueue.remove();

                // 尝试获取并移除此队列的头部元素
                // 如果队列为空则返回 null
//                LogEntity log = blockingQueue.poll();

                // 获取并移除此队列的头部元素，在指定的时间内等待可用的元素
                // 如果在超时时间内仍无元素可用则返回 null
//                LogEntity log = blockingQueue.poll(30, TimeUnit.SECONDS);

                // 获取并移除此队列的头部元素，如果队列为空则等待直到有元素为止
                LogEntity log = blockingQueue.take();

                logMapper.insert(log);
            } catch (Exception e) {
                log.error("日志记录异常！", e);
            }
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        new Thread(this).start();
    }

}
