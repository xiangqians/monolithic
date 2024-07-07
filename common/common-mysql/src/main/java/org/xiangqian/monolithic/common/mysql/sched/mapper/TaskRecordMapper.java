package org.xiangqian.monolithic.common.mysql.sched.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xiangqian.monolithic.common.mysql.LazyList;
import org.xiangqian.monolithic.common.mysql.Page;
import org.xiangqian.monolithic.common.mysql.sched.entity.TaskRecordEntity;

/**
 * 任务调度记录表Mapper
 *
 * @author xiangqian
 * @date 16:36 2024/06/23
 */
@Mapper
public interface TaskRecordMapper extends BaseMapper<TaskRecordEntity> {

    Page<TaskRecordEntity> page(Page<TaskRecordEntity> page, @Param("entity") TaskRecordEntity entity);

    LazyList<TaskRecordEntity> lazyList(LazyList<TaskRecordEntity> lazyList, @Param("entity") TaskRecordEntity entity);

}
