package org.xiangqian.monolithic.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;

/**
 * @author xiangqian
 * @date 19:55 2024/06/14
 */
@Component
public class MethodHandlerInterceptor extends AbsHandlerInterceptor {

    @Autowired
    private ApplicationContext applicationContext;

    private MethodSecurity methodSecurity;

    @Override
    protected boolean preHandle(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) throws Exception {
        if (methodSecurity == null) {
            methodSecurity = applicationContext.getBean(MethodSecurity.class);
        }


        return super.preHandle(request, response, handlerMethod);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE;
    }

}
