package org.xiangqian.monolithic.common.biz.sched.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.common.biz.sched.service.TaskRecordService;
import org.xiangqian.monolithic.common.mybatis.LazyList;
import org.xiangqian.monolithic.common.mybatis.Page;
import org.xiangqian.monolithic.common.mysql.sched.entity.TaskRecordEntity;
import org.xiangqian.monolithic.common.mysql.sched.mapper.TaskRecordMapper;

import java.util.List;

/**
 * @author xiangqian
 * @date 21:25 2024/07/15
 */
@Service
public class TaskRecordServiceImpl implements TaskRecordService {

    @Autowired
    private TaskRecordMapper mapper;

    @Override
    public List<TaskRecordEntity> list(TaskRecordEntity entity) {
        return mapper.list(entity);
    }

    @Override
    public LazyList<TaskRecordEntity> lazyList(LazyList<TaskRecordEntity> lazyList, TaskRecordEntity entity) {
        return mapper.lazyList(lazyList, entity);
    }

    @Override
    public Page<TaskRecordEntity> page(Page<TaskRecordEntity> page, TaskRecordEntity entity) {
        return mapper.page(page, entity);
    }

}
