package org.xiangqian.monolithic.common.biz.sys.service;

import org.xiangqian.monolithic.common.mysql.LazyList;
import org.xiangqian.monolithic.common.mysql.Page;
import org.xiangqian.monolithic.common.mysql.sys.entity.LogEntity;

/**
 * 日志服务
 *
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
public interface LogService {

    Page<LogEntity> page(Page<LogEntity> page, LogEntity logEntity);

    LazyList<LogEntity> lazyList(LazyList<LogEntity> lazyList, LogEntity logEntity);

    void asyncSave(LogEntity logEntity);

}
