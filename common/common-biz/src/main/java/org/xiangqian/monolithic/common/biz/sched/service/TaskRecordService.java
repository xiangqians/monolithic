package org.xiangqian.monolithic.common.biz.sched.service;

import org.xiangqian.monolithic.common.mybatis.LazyList;
import org.xiangqian.monolithic.common.mybatis.Page;
import org.xiangqian.monolithic.common.mysql.sched.entity.TaskRecordEntity;

import java.util.List;

/**
 * @author xiangqian
 * @date 21:24 2024/07/15
 */
public interface TaskRecordService {

    List<TaskRecordEntity> list(TaskRecordEntity entity);

    LazyList<TaskRecordEntity> lazyList(LazyList<TaskRecordEntity> lazyList, TaskRecordEntity entity);

    Page<TaskRecordEntity> page(Page<TaskRecordEntity> page, TaskRecordEntity entity);

}
