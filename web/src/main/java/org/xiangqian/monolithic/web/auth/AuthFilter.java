package org.xiangqian.monolithic.web.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.JsonUtil;
import org.xiangqian.monolithic.biz.Redis;
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
    private Redis redis;

    @Value("${spring.profiles.active}")
    private String profile;

    @Override
    protected void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        String servletPath = request.getServletPath();
        log.debug("servletPath {}", servletPath);

        // 开发环境或测试环境，放行【接口文档请求】
        if (profile.equals("dev") || profile.equals("test")) {
            if (servletPath.startsWith("/v3/")
                    || servletPath.startsWith("/swagger-ui/")
                    || servletPath.startsWith("/api-docs")
                    || servletPath.equals("/doc.html")) {
                chain.doFilter(request, response);
                return;
            }
        }

        // 放行【获取令牌请求】
        if (servletPath.equals("/auth/token")) {
            chain.doFilter(request, response);
            return;
        }

        String authorization = StringUtils.trim(request.getHeader("Authorization"));
        if (StringUtils.isEmpty(authorization)) {
            unauthorized(response);
            return;
        }

        Object value = redis.string().get(authorization);
        if (value == null) {
            unauthorized(response);
            return;
        }

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
