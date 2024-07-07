package org.xiangqian.monolithic.common.biz.sys.service;

import org.xiangqian.monolithic.common.mysql.sys.entity.UserEntity;

/**
 * 本地线程用户服务
 *
 * @author xiangqian
 * @date 12:06 2024/07/07
 */
public interface ThreadLocalUserService {

    /**
     * 获取本地线程登录用户信息
     *
     * @return
     */
    UserEntity get();

    /**
     * 设置本地线程登录用户信息
     *
     * @param userEntity
     */
    void set(UserEntity userEntity);

}
