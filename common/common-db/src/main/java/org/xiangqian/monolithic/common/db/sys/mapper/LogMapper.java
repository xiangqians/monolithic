package org.xiangqian.monolithic.common.db.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xiangqian.monolithic.biz.LazyList;
import org.xiangqian.monolithic.biz.Page;
import org.xiangqian.monolithic.biz.sys.entity.LogEntity;

/**
 * 日志表Mapper
 *
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
@Mapper
public interface LogMapper extends BaseMapper<LogEntity> {

    LazyList<LogEntity> list(LazyList<LogEntity> list, @Param("log") LogEntity log);

    Page<LogEntity> page(Page<LogEntity> page, @Param("log") LogEntity log);

}
