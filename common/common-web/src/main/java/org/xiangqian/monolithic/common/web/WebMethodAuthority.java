package org.xiangqian.monolithic.common.web;

/**
 * Web方法权限
 *
 * @author xiangqian
 * @date 12:41 2024/07/07
 */
public class WebMethodAuthority {

    /**
     * 是否允许未经授权访问
     *
     * @param method 请求方法
     * @param path   请求路径
     * @return
     */
    public boolean isAllow(String method, String path) {
        return true;
    }

}
