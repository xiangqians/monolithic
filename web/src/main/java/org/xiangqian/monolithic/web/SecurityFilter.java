package org.xiangqian.monolithic.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.sys.entity.UserEntity;
import org.xiangqian.monolithic.biz.sys.service.UserService;
import org.xiangqian.monolithic.util.JsonUtil;

import java.io.IOException;

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

    @Autowired
    private MethodSecurity methodSecurity;

    @Value("${management.token}")
    private String token;

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

        // /actuator/*
        if (servletPath.startsWith("/actuator")) {
            String authorization = StringUtils.trim(request.getHeader("Authorization"));
            if (StringUtils.isNotEmpty(authorization) && authorization.startsWith("Bearer ") && token.equals(authorization.substring("Bearer ".length()))) {
                chain.doFilter(request, response);
            } else {
                unauthorized(response);
            }
            return;
        }

        if (!servletPath.startsWith("/api/")) {
            unauthorized(response);
            return;
        }

        // 允许未经授权访问
        if (methodSecurity.isAllow(request.getMethod(), servletPath)) {
            chain.doFilter(request, response);
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
