package org.xiangqian.monolithic.biz.auth.service;

import org.xiangqian.monolithic.biz.auth.vo.AuthTokenRequest;
import org.xiangqian.monolithic.biz.auth.vo.AuthTokenResponse;
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
     * @param authTokenRequest
     * @return 令牌
     */
    AuthTokenResponse token(AuthTokenRequest authTokenRequest);

    /**
     * 撤销令牌
     *
     * @return
     */
    Boolean revoke();

}
