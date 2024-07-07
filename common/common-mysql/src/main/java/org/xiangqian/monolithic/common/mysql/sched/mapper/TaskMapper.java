package org.xiangqian.monolithic.common.mysql.sched.mapper;

import org.xiangqian.monolithic.common.mysql.sched.entity.TaskEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务调度表Mapper
 *
 * @author xiangqian
 * @date 16:36 2024/06/23
 */
@Mapper
public interface TaskMapper extends BaseMapper<TaskEntity> {

}
