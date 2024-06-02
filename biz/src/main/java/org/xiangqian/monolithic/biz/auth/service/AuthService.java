package org.xiangqian.monolithic.biz.auth.service;

import org.xiangqian.monolithic.biz.auth.model.AuthTokenReq;
import org.xiangqian.monolithic.biz.auth.model.AuthTokenResp;
import org.xiangqian.monolithic.biz.sys.entity.UserEntity;

/**
 * 授权服务
 *
 * @author xiangqian
 * @date 17:03 2024/06/01
 */
public interface AuthService {
    /**
     * 判断令牌是否有效
     *
     * @param token
     * @return
     */
    UserEntity getUser(String token);

    /**
     * 获取令牌
     *
     * @param authTokenReq
     * @return 令牌
     */
    AuthTokenResp token(AuthTokenReq authTokenReq);

    /**
     * 撤销令牌
     *
     * @return
     */
    Boolean revoke();

}
