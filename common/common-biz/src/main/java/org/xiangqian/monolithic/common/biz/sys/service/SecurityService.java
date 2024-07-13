package org.xiangqian.monolithic.common.biz.sys.service;

import org.xiangqian.monolithic.common.mysql.sys.entity.UserEntity;

/**
 * 安全服务
 *
 * @author xiangqian
 * @date 12:06 2024/07/07
 */
public interface SecurityService {

    /**
     * 获取当前登录用户
     *
     * @return
     */
    UserEntity getUser();

    /**
     * 设置当前登录用户
     *
     * @param user
     */
    void setUser(UserEntity user);

    /**
     * 移除当前登录用户
     */
    void removeUser();

}
