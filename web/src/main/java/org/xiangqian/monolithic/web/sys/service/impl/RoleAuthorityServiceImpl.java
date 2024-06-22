package org.xiangqian.monolithic.web.sys.service.impl;

import org.xiangqian.monolithic.web.sys.mapper.RoleAuthorityMapper;
import org.xiangqian.monolithic.web.sys.service.RoleAuthorityService;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author xiangqian
 * @date 11:34 2024/06/10
 */
@Service
public class RoleAuthorityServiceImpl implements RoleAuthorityService {

    @Autowired
    private RoleAuthorityMapper mapper;

}
