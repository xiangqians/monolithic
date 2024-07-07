package org.xiangqian.monolithic.webmvc;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.xiangqian.monolithic.common.model.Code;
import org.xiangqian.monolithic.common.model.Result;
import org.xiangqian.monolithic.common.util.JsonUtil;
import org.xiangqian.monolithic.common.web.WebSecurityFilter;

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
public class WebmvcSecurityFilter extends WebSecurityFilter<HttpServletRequest> implements Filter {

    @Autowired
    private MethodHandler methodHandler;

    /**
     * 参考：{@link jakarta.servlet.http.HttpFilter#doFilter(jakarta.servlet.ServletRequest, jakarta.servlet.ServletResponse, jakarta.servlet.FilterChain)}
     *
     * @author xiangqian
     * @date 12:50 2024/07/07
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!(request instanceof HttpServletRequest)) {
            throw new ServletException(request + " not HttpServletRequest");
        } else if (!(response instanceof HttpServletResponse)) {
            throw new ServletException(response + " not HttpServletResponse");
        } else {
            doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
        }
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        /**
         * {@link org.springframework.web.util.ContentCachingRequestWrapper} 是 Spring Framework 提供的一个实用类，用于包装 HTTP 请求，以便能够多次读取请求体的内容。
         */
        request = new ContentCachingRequestWrapper(request);

        methodHandler.reset(request);

        // 是否放行
        if (filter(request)) {
            // 下一个过滤器
            chain.doFilter(request, response);
            return;
        }

        // 未授权
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(JsonUtil.serializeAsString(new Result<>(Code.UNAUTHORIZED)));
    }

    @Override
    protected String getMethod(HttpServletRequest request) {
        return request.getMethod();
    }

    @Override
    protected String getPath(HttpServletRequest request) {
        return request.getServletPath();
    }

    @Override
    protected String getAuthorization(HttpServletRequest request) {
        return StringUtils.trim(request.getHeader(HttpHeaders.AUTHORIZATION));
    }

}
