package org.xiangqian.monolithic.biz.auth;

import org.xiangqian.monolithic.biz.sys.entity.UserEntity;

/**
 * @author xiangqian
 * @date 22:15 2024/06/01
 */
public class AuthUtil {

    private static final ThreadLocal<UserEntity> threadLocal = new ThreadLocal<>();

    public static UserEntity getUser() {
        return threadLocal.get();
    }

    public static void setUser(UserEntity user) {
        threadLocal.set(user);
    }

}
