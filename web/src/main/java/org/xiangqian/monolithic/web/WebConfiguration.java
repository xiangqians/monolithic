package org.xiangqian.monolithic.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import org.xiangqian.monolithic.biz.sys.entity.AuthorityEntity;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * @author xiangqian
 * @date 21:15 2024/06/02
 */
@Configuration(proxyBeanMethods = false)
public class WebConfiguration implements WebMvcConfigurer {

    @Autowired
    private LogHandlerInterceptor logHandlerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry
                // 添加拦截器
                .addInterceptor(logHandlerInterceptor)
                // 配置拦截地址
                .addPathPatterns("/api/**");
    }

    /**
     * @param requestMappingHandlerMapping
     * @return Map<Method, Map < RequestMethod, AuthorityEntity>>
     */
    @Bean
    public Map<Method, Map<String, AuthorityEntity>> methodMap(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        Map<RequestMappingInfo, HandlerMethod> map = requestMappingHandlerMapping.getHandlerMethods();
        Map<Method, Map<String, AuthorityEntity>> methodMap = new HashMap<>(map.size(), 1f);
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
            RequestMappingInfo requestMappingInfo = entry.getKey();
            PathPatternsRequestCondition pathPatternsRequestCondition = requestMappingInfo.getPathPatternsCondition();
            if (pathPatternsRequestCondition == null) {
                continue;
            }

            String rem = "";
            HandlerMethod handlerMethod = entry.getValue();
            Tag tag = handlerMethod.getBeanType().getAnnotation(Tag.class);
            if (tag != null) {
                rem = toString(tag.name(), tag.description());
            }
            Operation operation = handlerMethod.getMethodAnnotation(Operation.class);
            if (operation != null) {
                String string = toString(operation.summary(), operation.description());
                if (StringUtils.isNotEmpty(string)) {
                    if (StringUtils.isNotEmpty(rem)) {
                        rem += " - " + string;
                    } else {
                        rem = string;
                    }
                }
            }

            byte allow = handlerMethod.getMethodAnnotation(Allow.class) == null ? 0 : (byte) 1;

            Map<String, AuthorityEntity> requestMethodMap = new HashMap<>(4, 1f);

            RequestMethodsRequestCondition methodsCondition = requestMappingInfo.getMethodsCondition();
            Set<RequestMethod> methods = Optional.ofNullable(methodsCondition).map(RequestMethodsRequestCondition::getMethods).orElse(null);
            Set<PathPattern> patterns = pathPatternsRequestCondition.getPatterns();
            PathPattern pattern = patterns.iterator().next();
            String path = pattern.getPatternString();
            if (path.startsWith("/api/")) {
                if (CollectionUtils.isNotEmpty(methods)) {
                    for (RequestMethod method : methods) {
                        AuthorityEntity authority = new AuthorityEntity();
                        authority.setMethod(method.name());
                        authority.setPath(path);
                        authority.setAllow(allow);
                        authority.setRem(rem);
                        authority.setDel((byte) 0);
                        requestMethodMap.put(authority.getMethod(), authority);
                    }
                } else {
                    AuthorityEntity authority = new AuthorityEntity();
                    authority.setMethod("");
                    authority.setPath(path);
                    authority.setAllow(allow);
                    authority.setRem(rem);
                    authority.setDel((byte) 0);
                    requestMethodMap.put(authority.getMethod(), authority);
                }
            }
            methodMap.put(handlerMethod.getMethod(), requestMethodMap);
        }
        return methodMap;
    }

    private String toString(String name, String description) {
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
    }

}
