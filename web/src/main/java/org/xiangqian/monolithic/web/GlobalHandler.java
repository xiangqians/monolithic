package org.xiangqian.monolithic.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import org.xiangqian.monolithic.biz.sys.mapper.AuthorityMapper;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 全局处理器
 *
 * @author xiangqian
 * @date 14:28 2024/06/01
 */
@Slf4j
@Aspect
@RestControllerAdvice
public class GlobalHandler implements ErrorController, ApplicationRunner {

    @Around("execution(public * org.xiangqian.monolithic.web..controller.*.*(..))) && (@annotation(org.springframework.web.bind.annotation.RequestMapping) || @annotation(org.springframework.web.bind.annotation.GetMapping) || @annotation(org.springframework.web.bind.annotation.PostMapping) || @annotation(org.springframework.web.bind.annotation.PutMapping) || @annotation(org.springframework.web.bind.annotation.DeleteMapping)))")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        log.debug("---------【around】-------- {}", method);

        return joinPoint.proceed();
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Response> handleException(Exception exception) {
        if (exception instanceof NoHandlerFoundException) {
            return response(Code.NOT_FOUND);
        }

        if (exception instanceof HttpRequestMethodNotSupportedException) {
            return response(Code.REQUEST_METHOD_NOT_SUPPORTED);
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
        return new ResponseEntity<>(new Response(code), HttpStatus.OK);
    }

    @Autowired
    private AuthorityMapper authorityMapper;

    @Autowired
    @Qualifier("methodAuthoritiesMap")
    private Map<Method, List<AuthorityEntity>> methodAuthoritiesMap;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 存在多节点部署问题！
        List<Long> authorityIds = new ArrayList<>(methodAuthoritiesMap.size());
        for (List<AuthorityEntity> authorities : methodAuthoritiesMap.values()) {
            for (AuthorityEntity authority : authorities) {
                AuthorityEntity queryAuthority = new AuthorityEntity();
                queryAuthority.setMethod(authority.getMethod());
                queryAuthority.setPath(authority.getPath());
                AuthorityEntity storedAuthority = authorityMapper.getOne(queryAuthority);
                if (storedAuthority == null) {
                    authorityMapper.insert(authority);
                } else {
                    authority.setId(storedAuthority.getId());
                    if (!storedAuthority.getAllow().equals(authority.getAllow())
                            || !storedAuthority.getRem().equals(authority.getRem())
                            || storedAuthority.getDel() == 1) {
                        authorityMapper.updById(authority);
                    }
                }
                authorityIds.add(authority.getId());
            }
        }
        authorityMapper.delete(new LambdaQueryWrapper<AuthorityEntity>().notIn(AuthorityEntity::getId, authorityIds));
    }

}
