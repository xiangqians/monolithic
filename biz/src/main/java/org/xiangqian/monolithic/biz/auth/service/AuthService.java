package org.xiangqian.monolithic.biz.auth.service;

import org.xiangqian.monolithic.biz.auth.vo.AuthRequest;

/**
 * 授权服务
 *
 * @author xiangqian
 * @date 17:03 2024/06/01
 */
public interface AuthService {

    /**
     * 获取令牌
     *
     * @param authRequest
     * @return 令牌
     */
    String token(AuthRequest authRequest);

    /**
     * 撤销令牌
     *
     * @return
     */
    Boolean revoke();

}
