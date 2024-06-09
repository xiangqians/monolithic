package org.xiangqian.monolithic.web;

import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.SneakyThrows;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.xiangqian.monolithic.biz.sys.entity.AuthorityEntity;
import org.xiangqian.monolithic.biz.sys.entity.UserEntity;
import org.xiangqian.monolithic.biz.sys.service.UserService;
import org.xiangqian.monolithic.util.JsonUtil;
import org.xiangqian.monolithic.util.ServletUtil;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * 日志处理拦截器
 *
 * @author xiangqian
 * @date 16:32 2024/06/08
 */
@Component
public class LogHandlerInterceptor implements HandlerInterceptor {

    private final String name = "__startTime__";

    //    @Autowired
//    @Qualifier("methodMap")
    private Map<Method, Map<String, AuthorityEntity>> methodMap;

    @Autowired
    private UserService userService;

    /**
     * 在请求处理之前被调用
     *
     * @param request
     * @param response
     * @param handler
     * @return true：表示拦截器链继续执行，即请求将继续向下执行，交由后续的拦截器或处理器处理。false：表示拦截器链中断，即请求处理终止，后续的拦截器不再执行，也不会交由处理器处理。
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        request.setAttribute(name, System.currentTimeMillis());
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
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        afterCompletion(request, response, handlerMethod, e);
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception e) throws Exception {
        long startTime = (long) request.getAttribute(name);
        long endTime = System.currentTimeMillis();

        Method method = handlerMethod.getMethod();
        Map<String, AuthorityEntity> requestMethodMap = methodMap.get(method);
        String requestMethod = request.getMethod();
        AuthorityEntity authority = requestMethodMap.get(requestMethod);
        if (authority == null) {
            authority = requestMethodMap.get("");
        }

        UserEntity user = userService.get();
        Long userId = Optional.ofNullable(user).map(UserEntity::getId).orElse(null);


        String url = request.getRequestURL().toString();


        logger.info("Request URL: {}", );
        logger.info("Request Method: {}", request.getMethod());
        logger.info("Request Headers: {}", getRequestHeaders(request));
        logger.info("Request Body: {}", getRequestBody(request));

        Long userId = Optional.ofNullable(user).map(UserEntity::getId).orElse(0L);
        Long authorityId = Optional.ofNullable(authority.getId()).orElse(0L);
        String ip = null;
        int port = 0;

        if (ArrayUtils.isNotEmpty(args)) {
            boolean isAddComma = false;
            StringBuilder builder = new StringBuilder("[");
            for (Object arg : args) {
                // 排除序列化实例
                if (arg instanceof ServletRequest
                        || arg instanceof ServletResponse
                        || arg instanceof HttpSession) {
                    continue;
                }
                if (isAddComma) {
                    builder.append(",");
                }
                builder.append(JsonUtil.serializeAsString(arg));
                isAddComma = true;
            }
            builder.append("]");
        }


        String requestURL = request.getRequestURL().toString();
        String requestMethod = request.getMethod();
        String requestParams = request.getQueryString();

        String logMessage = String.format("Request URL: %s, Method: %s, Params: %s", requestURL, requestMethod, requestParams);
        System.out.println(handler);


        System.out.println(handler);
    }


    @SneakyThrows
    private void log(LocalDateTime dateTime, Method method, Object[] args, Object result, long time) {


    }

}
