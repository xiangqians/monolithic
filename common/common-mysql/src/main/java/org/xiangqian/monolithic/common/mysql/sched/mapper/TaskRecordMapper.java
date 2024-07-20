package org.xiangqian.monolithic.common.mysql.sched.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xiangqian.monolithic.common.mybatis.LazyList;
import org.xiangqian.monolithic.common.mybatis.Page;
import org.xiangqian.monolithic.common.mysql.sched.entity.TaskRecordEntity;

import java.util.List;

/**
 * 任务调度记录表Mapper
 *
 * @author xiangqian
 * @date 16:36 2024/06/23
 */
@Mapper
public interface TaskRecordMapper {

    List<TaskRecordEntity> list(@Param("entity") TaskRecordEntity entity);

    LazyList<TaskRecordEntity> lazyList(LazyList<TaskRecordEntity> lazyList, @Param("entity") TaskRecordEntity entity);

    Page<TaskRecordEntity> page(Page<TaskRecordEntity> page, @Param("entity") TaskRecordEntity entity);

}
