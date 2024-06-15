package org.xiangqian.monolithic.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.CodeException;
import org.xiangqian.monolithic.biz.sys.entity.AuthorityEntity;
import org.xiangqian.monolithic.biz.sys.entity.LogEntity;
import org.xiangqian.monolithic.biz.sys.entity.UserEntity;
import org.xiangqian.monolithic.biz.sys.mapper.LogMapper;
import org.xiangqian.monolithic.biz.sys.service.UserService;
import org.xiangqian.monolithic.util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 方法处理器
 * 1、方法权限处理
 * 2、方法异常处理
 * 3、方法日志处理
 *
 * @author xiangqian
 * @date 14:28 2024/06/01
 */
@Slf4j
@RestControllerAdvice
public class MethodHandler implements
        // 拦截器，在请求进入控制器方法之前、控制器方法执行之后以及请求完成之后执行特定的操作
        HandlerInterceptor,
        // 异常控制器
        ErrorController,
        // 用于对控制器方法的返回值进行处理并修改
        ResponseBodyAdvice<Object>,
        // Bean排序接口
        Ordered {

    private final String START_TIME = "__start_time__";
    private final String AUTHORITY = "__authority__";

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationContext applicationContext;

    private MethodSecurity methodSecurity;

    public void reset(HttpServletRequest request) {
        request.setAttribute(START_TIME, null);
        request.setAttribute(AUTHORITY, null);
        userService.setUser(null);
    }

    /**
     * 在请求处理之前被调用
     * <p>
     * 如果返回true，表示拦截器链继续执行，即请求将继续向下执行，交由后续的拦截器或处理器处理。
     * 如果返回false，表示拦截器链中断，即请求处理终止，后续的拦截器不再执行，也不会交由处理器处理。
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (methodSecurity == null) {
            methodSecurity = applicationContext.getBean(MethodSecurity.class);
        }

        request.setAttribute(START_TIME, System.currentTimeMillis());

        Method handleMethod = ((HandlerMethod) handler).getMethod();
        String method = request.getMethod();

        AuthorityEntity authority = methodSecurity.getAuthority(handleMethod, method);
        request.setAttribute(AUTHORITY, authority);
        if (authority == null) {
            throw new CodeException(Code.FORBIDDEN);
        }

        if (Byte.valueOf((byte) 0).equals(authority.getAllow())) {
            UserEntity user = userService.get();
            if (user == null) {
                throw new CodeException(Code.FORBIDDEN);
            }

            if (!user.isAdminRole()) {
                if (!methodSecurity.hasRoleId(handleMethod, method, user.getRoleId())) {
                    throw new CodeException(Code.FORBIDDEN);
                }
            }
        }

        return true;
    }

    /**
     * 在请求处理之后、视图渲染之前被调用
     *
     * @param request
     * @param response
     * @param handler
     * @param modelAndView
     * @throws Exception
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 在请求处理完成且视图渲染后被调用，无论是否发生异常
     *
     * @param request
     * @param response
     * @param handler
     * @param e
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception e) throws Exception {
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception exception) {
        if (exception instanceof NoHandlerFoundException) {
            return response(Code.NOT_FOUND);
        }

        if (exception instanceof HttpRequestMethodNotSupportedException) {
            return response(Code.METHOD_NOT_ALLOWED);
        }

        if (exception instanceof CodeException) {
            return response(exception.getMessage());
        }

        if (exception instanceof MethodArgumentNotValidException) {
            MethodArgumentNotValidException methodArgumentNotValidException = (MethodArgumentNotValidException) exception;
            BindingResult bindingResult = methodArgumentNotValidException.getBindingResult();
            FieldError fieldError = bindingResult.getFieldError();
            return response(fieldError.getDefaultMessage());
        }

        log.error("", exception);
        return response(Code.ERROR);
    }

    private ResponseEntity<Response> response(String code) {
        Response result = new Response(code);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    /**
     * 用于确定该 {@link org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice} 是否适用于给定的返回类型和消息转换器类型
     * <p>
     * 如果返回true，则会调用其他方法对返回值进行处理。
     * 如果返回false，则不会执行后续的处理方法。
     *
     * @param returnType    返回类型
     * @param converterType 消息转换器类型
     * @return
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    /**
     * 在将对象写入响应之前调用
     * <p>
     * 可以在此方法中对返回值进行修改和处理，并返回修改后的结果。
     * 常见的用途包括修改返回值、添加额外的字段、加密等操作。
     *
     * @param body
     * @param returnType
     * @param selectedContentType
     * @param selectedConverterType
     * @param request
     * @param response
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        try {
            log(((ServletServerHttpRequest) request).getServletRequest(), body instanceof Response ? (Response) body : null);
        } catch (Throwable t) {
            log.error("", t);
        }
        return body;
    }

    public void log(HttpServletRequest request, Response response) throws IOException {
        // 不记录日志接口日志
        String servletPath = request.getServletPath();
        if (servletPath.startsWith("/api/sys/log/")) {
            return;
        }

        Long startTime = (Long) request.getAttribute(START_TIME);
        if (startTime == null) {
            return;
        }

        LogEntity log = new LogEntity();
        log.setUserId(Optional.ofNullable(userService.get()).map(UserEntity::getId).orElse(null));
        log.setAuthorityId(Optional.ofNullable((AuthorityEntity) request.getAttribute(AUTHORITY)).map(AuthorityEntity::getId).orElse(null));

        String address = request.getRemoteAddr() + ":" + request.getRemotePort();
        log.setAddress(address);

        log.setMethod(request.getMethod());

        String url = request.getRequestURL().toString();
        String query = StringUtils.trim(request.getQueryString());
        if (StringUtils.isNotEmpty(query)) {
            url += "?" + query;
        }
        log.setUrl(url);

        String body = null;
        String contentType = StringUtils.trim(request.getContentType());
        if (StringUtils.isNotEmpty(contentType) && request instanceof ContentCachingRequestWrapper) {
            ContentCachingRequestWrapper contentCachingRequestWrapper = (ContentCachingRequestWrapper) request;

            // JSON内容类型
            if (contentType.startsWith("application/json")) {
                body = new String(contentCachingRequestWrapper.getContentAsByteArray(), contentCachingRequestWrapper.getCharacterEncoding());
                try {
                    body = JsonUtil.serializeAsString(JsonUtil.deserialize(body));
                } catch (Exception e) {
                }
            }
            // 表单内容类型
            else if (contentType.startsWith("application/x-www-form-urlencoded")) {
                body = contentCachingRequestWrapper.getContentAsByteArray().length + " Byte";
            }
        }
        log.setBody(body);

        log.setCode(Optional.ofNullable(response).map(Response::getCode).orElse(null));

        long endTime = System.currentTimeMillis();
        log.setTime((int) (endTime - startTime));

        logMapper.insert(log);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
