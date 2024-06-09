package org.xiangqian.monolithic.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * @author xiangqian
 * @date 12:57 2024/06/09
 */
public abstract class AbsHandlerInterceptor implements HandlerInterceptor, Ordered {

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
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        return preHandle(request, response, handlerMethod);
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

    protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        return true;
    }

    protected void afterCompletion(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod, Exception e) throws Exception {
    }

}
