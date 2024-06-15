package org.xiangqian.monolithic.biz.sys.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.biz.LazyList;
import org.xiangqian.monolithic.biz.Page;
import org.xiangqian.monolithic.biz.sys.entity.LogEntity;
import org.xiangqian.monolithic.biz.sys.mapper.LogMapper;
import org.xiangqian.monolithic.biz.sys.service.LogService;

/**
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper logMapper;

    @Override
    public LazyList<LogEntity> list(LazyList<LogEntity> list, LogEntity log) {
        return logMapper.list(list, log);
    }

    @Override
    public Page<LogEntity> page(Page<LogEntity> page, LogEntity log) {
        return logMapper.page(page, log);
    }

}
