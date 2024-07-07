package org.xiangqian.monolithic.common.biz.sys.service.impl;

import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.common.biz.sys.service.ThreadLocalUserService;
import org.xiangqian.monolithic.common.mysql.sys.entity.UserEntity;

/**
 * @author xiangqian
 * @date 12:07 2024/07/07
 */
@Service
public class ThreadLocalUserServiceImpl implements ThreadLocalUserService {

    private ThreadLocal<UserEntity> threadLocal;

    public ThreadLocalUserServiceImpl() {
        this.threadLocal = new ThreadLocal<>();
    }

    @Override
    public UserEntity get() {
        return threadLocal.get();
    }

    @Override
    public void set(UserEntity userEntity) {
        threadLocal.set(userEntity);
    }

}
