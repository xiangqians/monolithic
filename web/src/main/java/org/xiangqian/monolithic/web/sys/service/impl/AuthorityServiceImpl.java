package org.xiangqian.monolithic.web.sys.service.impl;

import org.xiangqian.monolithic.web.sys.mapper.AuthorityMapper;
import org.xiangqian.monolithic.web.sys.service.AuthorityService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiangqian
 * @date 13:02 2024/06/02
 */
@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    private AuthorityMapper mapper;

}
