package org.xiangqian.monolithic.common.web;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.xiangqian.monolithic.common.biz.sys.service.ThreadLocalUserService;
import org.xiangqian.monolithic.common.biz.sys.service.UserService;
import org.xiangqian.monolithic.common.mysql.sys.entity.UserEntity;

/**
 * @author xiangqian
 * @date 12:11 2024/07/07
 */
@Slf4j
public abstract class WebSecurityFilter<T> {

    @Value("${springdoc.api-docs.enabled}")
    private boolean enabled;

    @Value("${management.token}")
    private String token;

    @Autowired
    private UserService userService;

    @Autowired
    private ThreadLocalUserService threadLocalUserService;

    @Autowired
    private WebMethodAuthority webMethodAuthority;

    /**
     * 请求过滤
     *
     * @param t 请求信息
     * @return 是否放行
     */
    public boolean filter(T t) {
        String path = getPath(t);
        log.debug("path {}", path);

        // 如果开启接口文档，则放行接口文档请求
        if (enabled
                && (path.startsWith("/swagger-ui/")
                || path.startsWith("/v3/api-docs")
                || path.equals("/doc.html")
                || path.startsWith("/webjars"))) {
            return true;
        }

        // /actuator/*
        if (path.startsWith("/actuator")) {
            String token = getToken(t);
            if (StringUtils.isNotEmpty(token) && token.equals(this.token)) {
                return true;
            }
            return false;
        }

        if (!path.startsWith("/api/")) {
            return false;
        }

        // 允许未经授权访问
        if (webMethodAuthority.isAllow(getMethod(t), path)) {
            threadLocalUserService.set(null);
            return true;
        }

        String token = getToken(t);
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        UserEntity userEntity = userService.getByToken(token);
        if (userEntity == null) {
            return false;
        }

        threadLocalUserService.set(userEntity);
        return true;
    }

    protected abstract String getMethod(T t);

    protected abstract String getPath(T t);

    protected String getToken(T t) {
        String authorization = getAuthorization(t);
        if (StringUtils.isNotEmpty(authorization) && authorization.startsWith("Bearer ")) {
            return authorization.substring("Bearer ".length()).trim();
        }
        return null;
    }

    protected abstract String getAuthorization(T t);

}
