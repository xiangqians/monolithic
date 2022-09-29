package org.monolithic.handler;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.monolithic.o.Vpo;
import org.monolithic.resp.Response;
import org.monolithic.resp.DefaultStatusCode;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

/**
 * 全局处理器
 * <p>
 * {@link javax.validation.Valid} 先于 {@link Aspect} 执行
 * {@link RequestResponseBodyMethodProcessor#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)}
 *
 * @author xiangqian
 * @date 23:27 2022/08/16
 */
@Slf4j
@Aspect
@RestControllerAdvice(basePackages = {"org.monolithic.controller"})
public class GlobalHandler {

    /**
     * 'controller..' 表示controller包及其子包
     */
    @Pointcut("execution(public * org.monolithic.controller..*.*(..)) && (@annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping))")
    public void pointcut() {
    }

    @Around("pointcut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        if (ArrayUtils.isNotEmpty(args)) {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Class[] parameterTypes = methodSignature.getParameterTypes();
            for (int i = 0, length = args.length; i < length; i++) {
                Object arg = args[i];
                if (arg == null) {
                    continue;
                }

                if (arg instanceof Vpo) {
                    Vpo voParam = (Vpo) arg;
                    voParam.post();
                } else if (parameterTypes[i] == String.class) {
                    args[i] = StringUtils.trimToNull(arg.toString());
                }
            }
        }
        return joinPoint.proceed();
    }

    /**
     * internal server error
     *
     * @param exception
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public Response<?> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return Response.builder()
                .statusCode(DefaultStatusCode.INTERNAL_SERVER_ERROR)
                .message(exception.getMessage())
                .build();
    }

}
