package org.xiangqian.monolithic.biz.sys.mapper;

import org.xiangqian.monolithic.biz.sys.entity.LogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 日志表Mapper
 *
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
@Mapper
public interface LogMapper extends BaseMapper<LogEntity> {

}
