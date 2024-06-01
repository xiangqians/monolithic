package org.xiangqian.monolithic.web.auth;

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
import org.xiangqian.monolithic.biz.JsonUtil;
import org.xiangqian.monolithic.biz.auth.AuthUtil;
import org.xiangqian.monolithic.biz.auth.service.AuthService;
import org.xiangqian.monolithic.biz.sys.entity.UserEntity;
import org.xiangqian.monolithic.web.Response;

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
public class AuthFilter extends HttpFilter {

    @Autowired
    private AuthService authService;

    @Value("${springdoc.api-docs.enabled}")
    private Boolean enabledApiDoc;

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
        if (servletPath.equals("/auth/token")) {
            chain.doFilter(request, response);
            return;
        }

        String token = StringUtils.trim(request.getHeader("Authorization"));
        if (StringUtils.isEmpty(token)) {
            unauthorized(response);
            return;
        }

        UserEntity user = authService.getUser(token);
        if (user == null) {
            unauthorized(response);
            return;
        }

        user.setToken(token);
        AuthUtil.setUser(user);

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
