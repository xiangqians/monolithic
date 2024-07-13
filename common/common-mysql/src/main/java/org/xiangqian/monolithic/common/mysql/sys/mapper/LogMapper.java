package org.xiangqian.monolithic.common.mysql.sys.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.xiangqian.monolithic.common.mybatis.LazyList;
import org.xiangqian.monolithic.common.mybatis.Page;
import org.xiangqian.monolithic.common.mysql.sys.entity.LogEntity;

/**
 * 日志表Mapper
 *
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
@Mapper
public interface LogMapper extends BaseMapper<LogEntity> {

    Page<LogEntity> page(Page<LogEntity> page, @Param("entity") LogEntity entity);

    LazyList<LogEntity> lazyList(LazyList<LogEntity> lazyList, @Param("entity") LogEntity entity);

}
