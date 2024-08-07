package org.xiangqian.monolithic.common.biz.sys.service;

import org.xiangqian.monolithic.common.biz.sys.model.UserTokenEmailArg;
import org.xiangqian.monolithic.common.biz.sys.model.UserTokenPhoneArg;
import org.xiangqian.monolithic.common.biz.sys.model.UserTokenResult;
import org.xiangqian.monolithic.common.mysql.sys.entity.UserEntity;

/**
 * 用户服务
 *
 * @author xiangqian
 * @date 17:03 2024/06/01
 */
public interface UserService {

    /**
     * 根据邮箱获取令牌
     *
     * @param arg
     * @return
     */
    UserTokenResult getTokenByEmail(UserTokenEmailArg arg);

    /**
     * 根据手机号获取令牌
     *
     * @param arg
     * @return
     */
    UserTokenResult getTokenByPhone(UserTokenPhoneArg arg);

    /**
     * 根据令牌获取用户信息
     *
     * @param token
     * @return
     */
    UserEntity getByToken(String token);

    /**
     * 撤销令牌
     *
     * @return
     */
    Boolean revokeToken();

}
