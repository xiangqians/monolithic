package org.xiangqian.monolithic.webmvc;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.xiangqian.monolithic.common.biz.sys.service.SecurityService;
import org.xiangqian.monolithic.common.model.Code;
import org.xiangqian.monolithic.common.model.Result;
import org.xiangqian.monolithic.common.mysql.sys.entity.UserEntity;
import org.xiangqian.monolithic.common.util.JsonUtil;
import org.xiangqian.monolithic.common.web.WebSecurityFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 * 安全验证过滤器
 *
 * @author xiangqian
 * @date 13:42 2024/06/01
 */
@Slf4j
@WebFilter(urlPatterns = "/*") // 拦截所有路径
public class WebmvcSecurityFilter extends WebSecurityFilter<HttpServletRequest> implements Filter {

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private SecurityService securityService;

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
//        request = new ContentCachingRequestWrapper(request);

        // 是否放行
        if (filter(request)) {
            // 下一个过滤器
            chain.doFilter(request, response);
            return;
        }

        // 未授权
        writeAndFlush(response, new Result<>(Code.UNAUTHORIZED));
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
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        return StringUtils.trim(authorization);
    }

    @SneakyThrows
    @Override
    protected Method getHandleMethod(HttpServletRequest request) {
        HandlerExecutionChain handlerExecutionChain = requestMappingHandlerMapping.getHandler(request);
        if (handlerExecutionChain == null) {
            return null;
        }

        Object handler = handlerExecutionChain.getHandler();
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            return handlerMethod.getMethod();
        }

        return null;
    }

    @Override
    protected void setUser(HttpServletRequest request, UserEntity user) {
        securityService.setUser(user);
    }

    public static void writeAndFlush(HttpServletResponse response, Result<?> result) throws IOException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(CharEncoding.UTF_8);
        PrintWriter writer = response.getWriter();
        writer.write(JsonUtil.serializeAsString(result));
        writer.flush();
    }

}
