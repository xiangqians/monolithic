package org.xiangqian.monolithic.web;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import org.xiangqian.monolithic.biz.Code;
import org.xiangqian.monolithic.biz.CodeException;
import org.xiangqian.monolithic.biz.sys.entity.AuthorityEntity;
import org.xiangqian.monolithic.biz.sys.mapper.AuthorityMapper;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;

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
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @Autowired
    private AuthorityMapper authorityMapper;

    private Map<Method, List<AuthorityEntity>> methodAuthoritiesMap;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        BiFunction<String, String, String> toStringFunc = (name, description) -> {
            name = StringUtils.trimToEmpty(name);
            description = StringUtils.trimToEmpty(description);
            if (StringUtils.isEmpty(name)) {
                return description;
            }

            String string = name;
            if (StringUtils.isNotEmpty(description)) {
                string += "（" + description + "）";
            }
            return string;
        };

        methodAuthoritiesMap = new HashMap<>(64, 1f);
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            RequestMappingInfo reqMappingInfo = entry.getKey();
            PathPatternsRequestCondition reqCondition = reqMappingInfo.getPathPatternsCondition();
            if (Objects.isNull(reqCondition)) {
                continue;
            }

            String rem = "";
            HandlerMethod handlerMethod = entry.getValue();
            Tag tag = handlerMethod.getBeanType().getAnnotation(Tag.class);
            if (tag != null) {
                rem = toStringFunc.apply(tag.name(), tag.description());
            }
            Operation operation = handlerMethod.getMethodAnnotation(Operation.class);
            if (operation != null) {
                String string = toStringFunc.apply(operation.summary(), operation.description());
                if (StringUtils.isNotEmpty(string)) {
                    if (StringUtils.isNotEmpty(rem)) {
                        rem += " - " + string;
                    } else {
                        rem = string;
                    }
                }
            }

            RequestMethodsRequestCondition methodsCondition = reqMappingInfo.getMethodsCondition();
            Set<RequestMethod> methods = Optional.ofNullable(methodsCondition).map(RequestMethodsRequestCondition::getMethods).orElse(null);
            Set<PathPattern> patterns = reqCondition.getPatterns();
            List<AuthorityEntity> authorities = new ArrayList<>(patterns.size());
            for (PathPattern pattern : patterns) {
                String path = pattern.getPatternString();
                if (path.startsWith("/api/")) {
                    if (CollectionUtils.isNotEmpty(methods)) {
                        for (RequestMethod method : methods) {
                            AuthorityEntity authority = new AuthorityEntity();
                            authority.setMethod(method.name());
                            authority.setPath(path);
                            authority.setRem(rem);
                            authority.setDel((byte) 0);
                            authorities.add(authority);
                        }
                    } else {
                        AuthorityEntity authority = new AuthorityEntity();
                        authority.setMethod("");
                        authority.setPath(path);
                        authority.setRem(rem);
                        authority.setDel((byte) 0);
                        authorities.add(authority);
                    }
                }
            }
            methodAuthoritiesMap.put(handlerMethod.getMethod(), authorities);
        }

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
                    if (!storedAuthority.getRem().equals(authority.getRem()) || storedAuthority.getDel() == 1) {
                        authorityMapper.updById(authority);
                    }
                }
                authorityIds.add(authority.getId());
            }
        }
        authorityMapper.delete(new LambdaQueryWrapper<AuthorityEntity>().notIn(AuthorityEntity::getId, authorityIds));
    }

}
