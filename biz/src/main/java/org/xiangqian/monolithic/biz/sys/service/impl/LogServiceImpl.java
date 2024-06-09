package org.xiangqian.monolithic.biz.sys.service.impl;

import org.xiangqian.monolithic.biz.sys.mapper.LogMapper;
import org.xiangqian.monolithic.biz.sys.service.LogService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
@Service
public class LogServiceImpl implements LogService {

    @Autowired
    private LogMapper mapper;

}
