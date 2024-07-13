package org.xiangqian.monolithic.common.biz.sys.service;

import java.util.Set;

/**
 * 角色权限服务
 *
 * @author xiangqian
 * @date 11:34 2024/06/10
 */
public interface RoleAuthorityService {

    /**
     * 添加角色和权限集合的关联
     *
     * @param roleId
     * @param authorityIds
     */
    void add(Long roleId, Set<Long> authorityIds);

    /**
     * 判断角色是否有权限
     *
     * @param roleId
     * @param authorityId
     * @return
     */
    Boolean has(Long roleId, Long authorityId);

}
