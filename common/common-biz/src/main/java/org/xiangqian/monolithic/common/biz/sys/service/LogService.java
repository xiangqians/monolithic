package org.xiangqian.monolithic.common.biz.sys.service;

import org.xiangqian.monolithic.common.mybatis.LazyList;
import org.xiangqian.monolithic.common.mybatis.Page;
import org.xiangqian.monolithic.common.mysql.sys.entity.LogEntity;

/**
 * 日志服务
 *
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
public interface LogService {

    Page<LogEntity> page(Page<LogEntity> page, LogEntity entity);

    LazyList<LogEntity> lazyList(LazyList<LogEntity> lazyList, LogEntity entity);

    void asyncSave(LogEntity entity);

}
