package org.xiangqian.monolithic.common.biz.sys.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.common.biz.sys.service.LogService;
import org.xiangqian.monolithic.common.biz.sys.service.SecurityService;
import org.xiangqian.monolithic.common.mybatis.LazyList;
import org.xiangqian.monolithic.common.mybatis.Page;
import org.xiangqian.monolithic.common.mysql.sys.entity.LogEntity;
import org.xiangqian.monolithic.common.mysql.sys.mapper.LogMapper;

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

    @Autowired
    private SecurityService securityService;

    // 阻塞式队列
    private BlockingQueue<LogEntity> blockingQueue;

    public LogServiceImpl() {
        // 基于数组的有界阻塞队列
        this.blockingQueue = new ArrayBlockingQueue<>(1024 * 1024);
    }

    @Override
    public Page<LogEntity> page(Page<LogEntity> page, LogEntity entity) {
        log.debug("-----------------{}", securityService.getUser());

//        int i = 1/0;

        return logMapper.page(page, entity);
    }

    @Override
    public LazyList<LogEntity> lazyList(LazyList<LogEntity> lazyList, LogEntity entity) {
        return logMapper.lazyList(lazyList, entity);
    }

    @Override
    public void asyncSave(LogEntity entity) {
        // 尝试将指定的元素插入此队列
        // 如果插入成功则返回 true，如果队列已满则抛出 IllegalStateException
//        blockingQueue.add(entity);

        // 尝试将指定的元素插入此队列
        // 如果插入成功则返回 true，如果队列已满则返回 false
        if (!blockingQueue.offer(entity)) {
            log.warn("日志阻塞式队列已满，无法添加日志信息：{}", entity);
        }

        // 尝试在指定的时间内将指定的元素插入此队列
        // 如果插入成功则返回 true，如果在超时时间内仍无法插入则返回 false
//        blockingQueue.offer(entity, 30L, TimeUnit.SECONDS);

        // 将指定的元素插入此队列，如果队列已满则等待直到有空间为止
//        blockingQueue.put(entity);
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 获取但不移除此队列的头部元素
                // 如果队列为空则抛出 NoSuchElementException
//                LogEntity entity = blockingQueue.element();

                // 获取但不移除此队列的头部元素
                // 如果队列为空则返回 null
//                LogEntity entity = blockingQueue.peek();

                // --------------

                // 获取并移除此队列的头部元素
                // 如果队列为空则抛出 NoSuchElementException。
//                LogEntity entity = blockingQueue.remove();

                // 尝试获取并移除此队列的头部元素
                // 如果队列为空则返回 null
//                LogEntity entity = blockingQueue.poll();

                // 获取并移除此队列的头部元素，在指定的时间内等待可用的元素
                // 如果在超时时间内仍无元素可用则返回 null
//                LogEntity entity = blockingQueue.poll(30, TimeUnit.SECONDS);

                // 获取并移除此队列的头部元素，如果队列为空则等待直到有元素为止
                LogEntity entity = blockingQueue.take();

//                logMapper.insert(entity);
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
