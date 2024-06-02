package org.xiangqian.monolithic.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import org.xiangqian.monolithic.biz.sys.entity.AuthorityEntity;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.BiFunction;

/**
 * @author xiangqian
 * @date 21:15 2024/06/02
 */
@Configuration(proxyBeanMethods = false)
public class MamConfiguration {

    @Bean
    public Map<Method, List<AuthorityEntity>> methodAuthoritiesMap(RequestMappingHandlerMapping requestMappingHandlerMapping) {
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

        Map<Method, List<AuthorityEntity>> methodAuthoritiesMap = new HashMap<>(64, 1f);
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

            byte allow = handlerMethod.getMethodAnnotation(Allow.class) == null ? 0 : (byte) 1;

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
                            authority.setAllow(allow);
                            authority.setRem(rem);
                            authority.setDel((byte) 0);
                            authorities.add(authority);
                        }
                    } else {
                        AuthorityEntity authority = new AuthorityEntity();
                        authority.setMethod("");
                        authority.setPath(path);
                        authority.setAllow(allow);
                        authority.setRem(rem);
                        authority.setDel((byte) 0);
                        authorities.add(authority);
                    }
                }
            }
            methodAuthoritiesMap.put(handlerMethod.getMethod(), authorities);
        }
        return methodAuthoritiesMap;
    }

}
