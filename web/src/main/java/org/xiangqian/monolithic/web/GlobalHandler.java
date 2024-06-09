package org.xiangqian.monolithic.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.CodeException;
import org.xiangqian.monolithic.biz.sys.entity.AuthorityEntity;
import org.xiangqian.monolithic.biz.sys.entity.LogEntity;
import org.xiangqian.monolithic.biz.sys.entity.UserEntity;
import org.xiangqian.monolithic.biz.sys.mapper.LogMapper;
import org.xiangqian.monolithic.biz.sys.service.UserService;
import org.xiangqian.monolithic.util.HttpServletUtil;
import org.xiangqian.monolithic.util.JsonUtil;

import java.lang.reflect.Method;
import java.util.*;

/**
 * 全局处理器
 * 1、日志处理器
 * 2、权限处理器
 * 3、异常处理器
 *
 * @author xiangqian
 * @date 14:28 2024/06/01
 */
@Slf4j
@Aspect
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalHandler implements ErrorController {

    private AntPathMatcher antPathMatcher;

    @Autowired
    @Qualifier("methodAuthoritiesMap")
    private Map<Method, List<AuthorityEntity>> methodAuthoritiesMap;

    @Autowired
    @Qualifier("methodRoleIdsMap")
    private Map<Method, Set<Long>> methodRoleIdsMap;

    @Autowired
    private UserService userService;

    @Autowired
    private LogMapper logMapper;

    public GlobalHandler() {
        this.antPathMatcher = new AntPathMatcher();
    }

    @Around("execution(public * org.xiangqian.monolithic.web..controller.*.*(..))) && (@annotation(org.springframework.web.bind.annotation.RequestMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping)))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        HttpServletRequest request = HttpServletUtil.getRequest();
        request.setAttribute(AttributeName.START_TIME, System.currentTimeMillis());

        AuthorityEntity authority = (AuthorityEntity) request.getAttribute(AttributeName.AUTHORITY);
        if (authority == null) {
            List<AuthorityEntity> authorities = methodAuthoritiesMap.get(method);
            String reqMethod = request.getMethod();
            String servletPath = request.getServletPath();
            for (AuthorityEntity e : authorities) {
                if ((reqMethod.equals(e.getMethod()) || "".equals(e.getMethod())) && antPathMatcher.match(e.getPath(), servletPath)) {
                    authority = e;
                    break;
                }
            }
        }

        request.setAttribute(AttributeName.AUTHORITY, authority);

        if (Byte.valueOf((byte) 0).equals(authority.getAllow())) {
            UserEntity user = userService.get();
            if (user == null) {
                throw new CodeException(Code.FORBIDDEN);
            }

            if (!user.isAdminRole()) {
                Set<Long> roleIds = methodRoleIdsMap.get(method);
                if (CollectionUtils.isEmpty(roleIds) || !roleIds.contains(user.getRoleId())) {
                    throw new CodeException(Code.FORBIDDEN);
                }
            }
        }

        Object result = joinPoint.proceed();
        log(result);
        return result;
    }

    private void log(Object result) {
        try {
            HttpServletRequest request = HttpServletUtil.getRequest();
            Long startTime = (Long) request.getAttribute(AttributeName.START_TIME);
            if (startTime == null) {
                return;
            }

            LogEntity log = new LogEntity();

            UserEntity user = userService.get();
            if (user != null) {
                log.setUserId(user.getId());
            }

            AuthorityEntity authority = (AuthorityEntity) request.getAttribute(AttributeName.AUTHORITY);
            if (authority != null) {
                log.setAuthorityId(authority.getId());
            }

            String code = null;
            Object respBody = null;
            if (result instanceof Response) {
                Response response = (Response) result;
                code = response.getCode();
                respBody = response;
            }
            log.setCode(code);

            String address = request.getRemoteAddr();
            log.setAddress(address);

            log.setReqMethod(request.getMethod());
            log.setReqUrl(request.getRequestURL().toString());

            Map<String, String> reqHeaderMap = new HashMap<>(16, 1f);
            Enumeration<String> reqHeaderNames = request.getHeaderNames();
            while (reqHeaderNames.hasMoreElements()) {
                String reqHeaderName = reqHeaderNames.nextElement();
                String reqHeaderValue = request.getHeader(reqHeaderName);
                reqHeaderMap.put(reqHeaderName, reqHeaderValue);
            }
            log.setReqHeader(JsonUtil.serializeAsString(reqHeaderMap));

            HttpServletResponse response = HttpServletUtil.getResponse();
            Collection<String> respHeaderNames = response.getHeaderNames();
            Map<String, String> respHeaderMap = new HashMap<>(respHeaderNames.size(), 1f);
            for (String respHeaderName : respHeaderNames) {
                String respHeaderValue = response.getHeader(respHeaderName);
                respHeaderMap.put(respHeaderName, respHeaderValue);
            }
            log.setRespHeader(JsonUtil.serializeAsString(respHeaderMap));

            if (respBody != null) {
                log.setRespBody(JsonUtil.serializeAsString(respBody));
            }

            long endTime = System.currentTimeMillis();
            log.setTime((int) (endTime - startTime));

            logMapper.insert(log);
        } catch (Throwable t) {
            log.error("", t);
        }
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
        log(result);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
