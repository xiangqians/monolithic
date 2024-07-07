package org.xiangqian.monolithic.webflux;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.reactive.result.condition.PatternsRequestCondition;
import org.springframework.web.reactive.result.condition.RequestMethodsRequestCondition;
import org.springframework.web.reactive.result.method.RequestMappingInfo;
import org.springframework.web.reactive.result.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.pattern.PathPattern;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityEntity;
import org.xiangqian.monolithic.common.mysql.sys.entity.AuthorityGroupEntity;
import org.xiangqian.monolithic.common.web.Allow;
import org.xiangqian.monolithic.common.web.WebDocConfiguration;

import java.lang.reflect.Method;
import java.util.*;

/**
 * @author xiangqian
 * @date 11:21 2024/07/07
 */
@Import(WebDocConfiguration.class)
@Configuration(proxyBeanMethods = false)
public class WebfluxAutoConfiguration {

    @Bean
    public List<AuthorityGroupEntity> authorityGroupEntities(RequestMappingHandlerMapping requestMappingHandlerMapping) {
        // 请求方法映射
        Map<RequestMappingInfo, HandlerMethod> handlerMethodMap = requestMappingHandlerMapping.getHandlerMethods();
        Map<String, AuthorityGroupEntity> authorityGroupEntityMap = new HashMap<>(handlerMethodMap.size(), 1f);
        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : handlerMethodMap.entrySet()) {
            Method handleMethod = entry.getValue().getMethod();

            // Class @RequestMapping 只映射一个路径
            Class<?> clazz = handleMethod.getDeclaringClass();
            String[] paths = Optional.ofNullable(clazz.getAnnotation(RequestMapping.class)).map(RequestMapping::value).orElse(null);
            String path = null;
            if (ArrayUtils.isEmpty(paths) || !(path = paths[0]).startsWith("/api/")) {
                continue;
            }

            AuthorityGroupEntity authorityGroupEntity = authorityGroupEntityMap.get(path);
            if (authorityGroupEntity == null) {
                authorityGroupEntity = new AuthorityGroupEntity();
                authorityGroupEntity.setPath(path);
                Tag tag = clazz.getAnnotation(Tag.class);
                if (tag != null) {
                    authorityGroupEntity.setRem(toString(tag.name(), tag.description()));
                }
                authorityGroupEntity.setDel((byte) 0);
                authorityGroupEntity.setAuthorities(new ArrayList<>(8));
                authorityGroupEntityMap.put(path, authorityGroupEntity);
            }

            RequestMappingInfo requestMappingInfo = entry.getKey();

            String rem = null;
            Operation operation = handleMethod.getAnnotation(Operation.class);
            if (operation != null) {
                rem = toString(operation.summary(), operation.description());
            }

            byte allow = handleMethod.getAnnotation(Allow.class) == null ? 0 : (byte) 1;

            // 请求方法
            Set<RequestMethod> requestMethods = Optional.ofNullable(requestMappingInfo.getMethodsCondition()).map(RequestMethodsRequestCondition::getMethods).orElse(null);
            if (CollectionUtils.isEmpty(requestMethods)) {
                requestMethods = Set.of(RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE);
            }

            // 请求路径
            PatternsRequestCondition patternsRequestCondition = requestMappingInfo.getPatternsCondition();
            Set<PathPattern> pathPatterns = patternsRequestCondition.getPatterns();
            // Method @RequestMapping 只映射一个路径
            PathPattern pathPattern = pathPatterns.iterator().next();
            path = pathPattern.getPatternString();

            List<AuthorityEntity> authorities = authorityGroupEntity.getAuthorities();
            for (RequestMethod requestMethod : requestMethods) {
                AuthorityEntity authority = new AuthorityEntity();
                authority.setMethod(requestMethod.name());
                authority.setPath(path);
                authority.setAllow(allow);
                authority.setRem(rem);
                authority.setDel((byte) 0);
                authority.setHandleMethod(handleMethod);
                authorities.add(authority);
            }
        }

        return authorityGroupEntityMap.values().stream().toList();
    }

    public static String toString(String name, String description) {
        name = StringUtils.trim(name);
        description = StringUtils.trim(description);
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
