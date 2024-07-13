package org.xiangqian.monolithic.common.biz.sys.service.impl;

import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.common.biz.sys.service.SecurityService;
import org.xiangqian.monolithic.common.mysql.sys.entity.UserEntity;

/**
 * @author xiangqian
 * @date 12:07 2024/07/07
 */
@Service
public class SecurityServiceImpl implements SecurityService {

    private ThreadLocal<UserEntity> threadLocal;

    public SecurityServiceImpl() {
        this.threadLocal = new ThreadLocal<>();
    }

    @Override
    public UserEntity getUser() {
        return threadLocal.get();
    }

    @Override
    public void setUser(UserEntity user) {
        threadLocal.set(user);
    }

    @Override
    public void removeUser() {
        threadLocal.remove();
    }

}
