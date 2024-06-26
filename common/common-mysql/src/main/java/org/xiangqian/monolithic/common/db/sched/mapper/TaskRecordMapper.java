package org.xiangqian.monolithic.common.db.sched.mapper;

import org.xiangqian.monolithic.common.db.sched.entity.TaskRecordEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务调度记录表Mapper
 *
 * @author xiangqian
 * @date 16:36 2024/06/23
 */
@Mapper
public interface TaskRecordMapper extends BaseMapper<TaskRecordEntity> {

}
