package org.xiangqian.monolithic.common.biz.sys.service;

/**
 * 权限服务
 *
 * @author xiangqian
 * @date 13:02 2024/06/02
 */
public interface AuthorityService {

    /**
     * 是否允许未经授权访问
     *
     * @param method 请求方法
     * @param path   请求路径
     * @return
     */
    boolean isAllow(String method, String path);

}
