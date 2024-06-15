package org.xiangqian.monolithic.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartFile;
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
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 方法处理器
 * 1、日志处理
 * 2、权限处理
 * 3、异常处理
 *
 * @author xiangqian
 * @date 14:28 2024/06/01
 */
@Slf4j
@Aspect
@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class MethodHandler implements ErrorController {

    @Autowired
    private UserService userService;

    @Autowired
    private LogMapper logMapper;

    @Autowired
    private MethodSecurity methodSecurity;

    private final String START_TIME = "__start_time__";
    private final String ARGS = "__args__";
    private final String AUTHORITY = "__authority__";

    @Around("execution(public * org.xiangqian.monolithic.web..controller.*.*(..))) && (@annotation(org.springframework.web.bind.annotation.RequestMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping)))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = HttpServletUtil.getRequest();
        request.setAttribute(START_TIME, System.currentTimeMillis());

        String method = request.getMethod();

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method handleMethod = methodSignature.getMethod();

        Object[] args = joinPoint.getArgs();
        request.setAttribute(ARGS, args);

        AuthorityEntity authority = methodSecurity.getAuthority(handleMethod, method);
        if (authority == null) {
            throw new CodeException(Code.FORBIDDEN);
        }

        request.setAttribute(AUTHORITY, authority);

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

        Object result = joinPoint.proceed();
        log(result);
        return result;
    }

    private void log(Object result) {
        try {
            HttpServletRequest request = HttpServletUtil.getRequest();
            Long startTime = (Long) request.getAttribute(START_TIME);
            if (startTime == null) {
                return;
            }

            // 不记录日志接口日志
            if (request.getServletPath().startsWith("/api/sys/log/")) {
                return;
            }

            LogEntity log = new LogEntity();

            UserEntity user = userService.get();
            if (user != null) {
                log.setUserId(user.getId());
            }

            AuthorityEntity authority = (AuthorityEntity) request.getAttribute(AUTHORITY);
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

            String address = request.getRemoteAddr() + ":" + request.getRemotePort();
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

            Object[] args = (Object[]) request.getAttribute(ARGS);
            if (ArrayUtils.isNotEmpty(args)) {
                StringBuilder reqBodyBuilder = new StringBuilder();
                for (Object arg : args) {
                    // 排除序列化实例
                    if (arg instanceof HttpServletRequest
                            || arg instanceof HttpServletResponse
                            || arg instanceof HttpSession
                            || arg instanceof MultipartFile) {
                        continue;
                    }
                    if (!reqBodyBuilder.isEmpty()) {
                        reqBodyBuilder.append(",");
                    }
                    reqBodyBuilder.append(JsonUtil.serializeAsString(arg));
                }
                log.setReqBody("[" + reqBodyBuilder + "]");
            }

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
