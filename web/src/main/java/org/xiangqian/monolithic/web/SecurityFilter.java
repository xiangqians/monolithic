package org.xiangqian.monolithic.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.util.AntPathMatcher;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.sys.entity.AuthorityEntity;
import org.xiangqian.monolithic.biz.sys.entity.UserEntity;
import org.xiangqian.monolithic.biz.sys.service.UserService;
import org.xiangqian.monolithic.util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 安全验证过滤器
 *
 * @author xiangqian
 * @date 13:42 2024/06/01
 */
@Slf4j
@WebFilter(urlPatterns = "/*") // 拦截所有路径
@Order(Ordered.HIGHEST_PRECEDENCE) // 设置执行顺序为最高优先级
public class SecurityFilter extends HttpFilter {

    @Autowired
    private UserService userService;

    @Value("${springdoc.api-docs.enabled}")
    private Boolean enabledApiDoc;

    @Value("${spring.boot.admin.secret}")
    private String secret;

    private Map<String, List<String>> allowMap;

    private AntPathMatcher antPathMatcher;

    public SecurityFilter() {
        this.antPathMatcher = new AntPathMatcher();
    }

    @Autowired
    @Qualifier("methodMap")
    public void setMethodMap(Map<Method, Map<String, AuthorityEntity>> methodMap) {
        allowMap = new HashMap<>(16, 1f);
        for (Map<String, AuthorityEntity> requestMethodMap : methodMap.values()) {
            for (AuthorityEntity authority : requestMethodMap.values()) {
                if (Byte.valueOf((byte) 1).equals(authority.getAllow())) {
                    String method = authority.getMethod();
                    String path = authority.getPath();
                    if (StringUtils.isNotEmpty(method)) {
                        addAllow(method, path);
                    } else {
                        addAllow(HttpMethod.GET.name(), path);
                        addAllow(HttpMethod.POST.name(), path);
                        addAllow(HttpMethod.PUT.name(), path);
                        addAllow(HttpMethod.DELETE.name(), path);
                    }
                }
            }
        }
    }

    private void addAllow(String method, String path) {
        List<String> paths = allowMap.get(method);
        if (paths == null) {
            paths = new ArrayList<>(8);
            allowMap.put(method, paths);
        }
        paths.add(path);
    }

    private boolean isAllow(String method, String path) {
        if (MapUtils.isEmpty(allowMap)) {
            return false;
        }

        List<String> paths = allowMap.get(method);
        if (CollectionUtils.isEmpty(paths)) {
            return false;
        }

        for (String pattern : paths) {
            if (antPathMatcher.match(pattern, path)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String servletPath = request.getServletPath();
        log.debug("servletPath {}", servletPath);

        // 如果开启接口文档，则放行【接口文档请求】
        if (BooleanUtils.toBoolean(enabledApiDoc)) {
            if (servletPath.startsWith("/swagger-ui/")
                    || servletPath.startsWith("/v3/api-docs")
                    || servletPath.equals("/doc.html")
                    || servletPath.startsWith("/webjars")) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 放行【获取令牌请求】
        if (servletPath.equals("/api/sys/user/token/email") || servletPath.equals("/api/sys/user/token/phone")) {
            chain.doFilter(request, response);
            return;
        }

        // 允许未经授权访问
        if (isAllow(request.getMethod(), servletPath)) {
            chain.doFilter(request, response);
            return;
        }

        // /actuator/*
        if (servletPath.startsWith("/actuator")) {
            if (secret.equals(StringUtils.trim(request.getHeader("Secret")))) {
                chain.doFilter(request, response);
            } else {
                unauthorized(response);
            }
            return;
        }

        String token = StringUtils.trim(request.getHeader("Authorization"));
        if (StringUtils.isEmpty(token) || !token.startsWith("Bearer ") || StringUtils.isEmpty(token = token.substring("Bearer ".length()))) {
            unauthorized(response);
            return;
        }

        UserEntity user = userService.getByToken(token);
        if (user == null) {
            unauthorized(response);
            return;
        }

        user.setToken(token);
        userService.setUser(user);

        // 放行
        chain.doFilter(request, response);
    }

    private void unauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JsonUtil.serializeAsString(new Response(Code.UNAUTHORIZED)));
    }

}
