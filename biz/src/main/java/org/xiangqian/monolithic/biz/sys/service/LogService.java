package org.xiangqian.monolithic.biz.sys.service;

import org.apache.ibatis.annotations.Param;
import org.xiangqian.monolithic.biz.LazyList;
import org.xiangqian.monolithic.biz.Page;
import org.xiangqian.monolithic.biz.sys.entity.LogEntity;

/**
 * 日志服务
 *
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
public interface LogService {

    LazyList<LogEntity> list(LazyList<LogEntity> list, LogEntity log);

    Page<LogEntity> page(Page<LogEntity> page, LogEntity log);

}
